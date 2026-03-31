package arena.battle;

import arena.GameUI;
import arena.GameUI.ActionChoice;
import arena.entity.Combatant;
import arena.entity.Enemy;
import arena.entity.Player;
import arena.item.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * BattleEngine — orchestrates the entire combat loop for one level.
 *
 * <p>Responsibilities (SRP):
 * <ul>
 *   <li>Managing round progression</li>
 *   <li>Determining turn order via speed stat</li>
 *   <li>Dispatching each combatant's turn</li>
 *   <li>Checking win / loss conditions after every action</li>
 *   <li>Triggering backup spawns at the correct time</li>
 * </ul>
 *
 * <p>This class knows nothing about how actions work internally — it delegates
 * to {@link ActionHandler} and reads results through return values (DIP, SRP).
 *
 * <p>DIP: depends on {@link GameUI} and {@link LevelConfig} abstractions;
 * never on concrete enemy or item classes.
 */
public class BattleEngine {

    private final GameUI ui;

    /**
     * Constructs a BattleEngine with the shared UI instance.
     *
     * @param ui the boundary object handling all display and input
     */
    public BattleEngine(GameUI ui) {
        this.ui = ui;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Runs a complete battle for the given player, item loadout, and level.
     *
     * <p>Returns {@code true} if the player wins, {@code false} if defeated.
     * The caller (Main / GameLoop) uses this to decide what screen to show next.
     *
     * @param player    the human-controlled combatant
     * @param items     the two items chosen at setup (may contain duplicates)
     * @param config    enemy wave configuration for the chosen difficulty
     * @return {@code true} = player victory, {@code false} = player defeat
     */
    public boolean run(Player player, List<Item> items, LevelConfig config) {

        // Active enemy list — starts with the initial wave
        List<Enemy> activeEnemies = new ArrayList<>(config.getInitialWave());
        boolean backupSpawned = false;
        int round = 0;

        while (true) {
            round++;
            ui.showRoundHeader(round);

            // ── Check backup spawn at START of round (spec §3.6) ─────────────
            // "Backup spawns after the initial wave is completely defeated."
            // All backup entities enter simultaneously at the start of the
            // next round after the initial wave is cleared.
            if (allDefeated(activeEnemies) && config.hasBackup() && !backupSpawned) {
                List<Enemy> backup = config.getBackupWave();
                activeEnemies.addAll(backup);
                backupSpawned = true;
                ui.showMessage("== BACKUP SPAWN! Reinforcements arrive simultaneously! ==");
                for (Enemy e : backup) {
                    ui.showMessage("  + " + e.getName() + " joins the battle!");
                }
            }

            // ── Immediate win (no enemies and no more backup) ─────────────────
            if (allDefeated(activeEnemies) && (!config.hasBackup() || backupSpawned)) {
                ui.showVictory(player, round);
                return true;
            }

            // ── Display battle state ──────────────────────────────────────────
            ui.showBattleState(player, activeEnemies, items);

            // ── Determine turn order (descending speed) ───────────────────────
            List<Combatant> turnOrder = buildTurnOrder(player, activeEnemies);

            // ── Execute each combatant's turn ─────────────────────────────────
            for (Combatant combatant : turnOrder) {

                // Skip combatants eliminated earlier this round
                if (!combatant.isAlive()) continue;

                // Apply + tick status effects BEFORE acting (spec §Screen Display)
                combatant.applyEffects();

                // Check if the effect killed the combatant (e.g. future poison)
                if (!combatant.isAlive()) continue;

                if (combatant instanceof Player p) {
                    // ── Player turn ───────────────────────────────────────────
                    handlePlayerTurn(p, items, activeEnemies);

                } else if (combatant instanceof Enemy e) {
                    // ── Enemy turn ────────────────────────────────────────────
                    handleEnemyTurn(e, player);
                }

                // ── Defeat check after every action ──────────────────────────
                if (!player.isAlive()) {
                    long remaining = activeEnemies.stream().filter(Enemy::isAlive).count();
                    ui.showDefeat((int) remaining, round);
                    return false;
                }

                // ── Mid-round win check ───────────────────────────────────────
                // If all enemies die during the round and no backup is left,
                // end immediately — do not wait for the round to finish.
                if (allDefeated(activeEnemies)) {
                    if (config.hasBackup() && !backupSpawned) {
                        // Backup will enter at start of NEXT round — keep looping
                        break;
                    } else {
                        ui.showVictory(player, round);
                        return true;
                    }
                }
            }

            // ── Post-round defeat check (edge case: AoE / effects) ───────────
            if (!player.isAlive()) {
                long remaining = activeEnemies.stream().filter(Enemy::isAlive).count();
                ui.showDefeat((int) remaining, round);
                return false;
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PLAYER TURN
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Handles a full player turn: prompt action → execute action.
     *
     * <p>Also decrements the special skill cooldown at the start of the
     * player's turn (spec: "decreasing cooldown only if a turn by the
     * combatant took place").
     *
     * @param player        the acting player
     * @param items         current item inventory (mutated when item is used)
     * @param activeEnemies list of all enemies (some may be dead)
     */
    private void handlePlayerTurn(Player player, List<Item> items, List<Enemy> activeEnemies) {

        // Tick special skill cooldown at start of player's turn
        player.tickCooldown();

        List<Enemy> aliveEnemies = getAliveEnemies(activeEnemies);

        // Keep prompting until the player makes a valid choice
        while (true) {
            ActionChoice choice = ui.chooseAction(!items.isEmpty());

            switch (choice) {

                case BASIC_ATTACK -> {
                    Enemy target = ui.chooseTarget(aliveEnemies);
                    if (target == null) continue;
                    int dmg = calcDamage(player, target);
                    target.takeDamage(dmg);
                    ui.showMessage(player.getName() + " attacks " + target.getName()
                            + " for " + dmg + " damage! HP: "
                            + target.getCurrentHp() + "/" + target.getMaxHp());
                    if (!target.isAlive()) {
                        ui.showMessage(target.getName() + " has been ELIMINATED!");
                    }
                    return; // turn consumed
                }

                case DEFEND -> {
                    player.applyDefend();
                    ui.showMessage(player.getName()
                            + " takes a defensive stance! DEF +10 for this turn and next.");
                    return;
                }

                case USE_ITEM -> {
                    if (items.isEmpty()) {
                        ui.showMessage("No items available! Choose a different action.");
                        continue; // re-prompt
                    }
                    int idx = ui.chooseItemIndex(items);
                    if (idx < 0 || idx >= items.size()) {
                        ui.showMessage("Invalid item selection.");
                        continue;
                    }
                    Item chosen = items.get(idx);
                    chosen.use(player, aliveEnemies, ui);
                    items.remove(idx);
                    ui.showMessage("Item used. Items remaining: " + items.size());
                    return;
                }

                case SPECIAL_SKILL -> {
                    if (!player.canUseSpecialSkill()) {
                        ui.showMessage("Special skill is on cooldown ("
                                + player.getCooldown() + " turn(s) remaining). "
                                + "Choose a different action.");
                        continue; // re-prompt
                    }
                    player.useSpecialSkill(aliveEnemies, ui);
                    return;
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ENEMY TURN
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Handles an enemy's turn.
     *
     * <p>Enemies always execute BasicAttack on the player (spec §4 vii).
     * If the enemy is stunned, {@link Combatant#applyEffects()} will have
     * already set its skip-turn flag — checked here via
     * {@link Enemy#isStunned()}.
     *
     * @param enemy  the acting enemy
     * @param player the target (always the player)
     */
    private void handleEnemyTurn(Enemy enemy, Player player) {

        if (enemy.isStunned()) {
            ui.showTurnSkipped(enemy);
            return;
        }

        int dmg = calcDamage(enemy, player);
        player.takeDamage(dmg);
        ui.showMessage(enemy.getName() + " attacks " + player.getName()
                + " for " + dmg + " damage! HP: "
                + player.getCurrentHp() + "/" + player.getMaxHp());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Builds the turn order for the current round sorted by descending speed.
     * Only alive combatants are included.
     *
     * @param player        the player
     * @param activeEnemies all enemies (alive and dead)
     * @return ordered list of alive combatants, highest speed first
     */
    private List<Combatant> buildTurnOrder(Player player, List<Enemy> activeEnemies) {
        List<Combatant> all = new ArrayList<>();
        if (player.isAlive()) all.add(player);
        for (Enemy e : activeEnemies) {
            if (e.isAlive()) all.add(e);
        }
        // Sort descending by speed — ties preserve insertion order (player first if equal)
        all.sort(Comparator.comparingInt(Combatant::getSpeed).reversed());
        return all;
    }

    /**
     * Calculates net damage from attacker to defender.
     * Formula: {@code max(0, attacker.ATK - defender.DEF)}
     *
     * <p>Respects the SmokeBomb effect on the player — if the player has
     * SmokeBomb active, enemy attacks deal 0 damage regardless of stats.
     *
     * @param attacker the combatant dealing damage
     * @param defender the combatant receiving damage
     * @return non-negative net damage value
     */
    private int calcDamage(Combatant attacker, Combatant defender) {
        // SmokeBomb check: if defender is a Player with smoke active, 0 damage
        if (defender instanceof Player p && p.hasSmokeBomb()) {
            ui.showMessage("Smoke Bomb absorbs the attack! 0 damage.");
            return 0;
        }
        return Math.max(0, attacker.getAttack() - defender.getDefense());
    }

    /**
     * Returns {@code true} if every enemy in the list is dead.
     *
     * @param enemies list to check
     * @return {@code true} when all enemies have {@code isAlive() == false}
     */
    private boolean allDefeated(List<Enemy> enemies) {
        return enemies.stream().noneMatch(Enemy::isAlive);
    }

    /**
     * Filters the enemy list to only alive enemies.
     *
     * @param enemies full enemy list
     * @return new list containing only alive enemies
     */
    private List<Enemy> getAliveEnemies(List<Enemy> enemies) {
        List<Enemy> alive = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.isAlive()) alive.add(e);
        }
        return alive;
    }
}
