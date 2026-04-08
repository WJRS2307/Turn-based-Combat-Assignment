package arena.engine;

import arena.ui.GameUI;
import arena.ui.GameUI.ActionChoice;
import arena.entity.Combatant;
import arena.entity.Enemy;
import arena.entity.Player;
import arena.item.Item;
import arena.item.ItemType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BattleEngine {

    private final GameUI ui;

    public BattleEngine(GameUI ui) {
        this.ui = ui;
    }

    public boolean run(Player player, List<Item> items, LevelConfig config) {
        List<Enemy> activeEnemies = new ArrayList<>(config.getInitialWave());
        boolean backupSpawned = false;
        int round = 0;

        while (true) {
            round++;
            ui.showRoundHeader(round);

            // Apply existing status effects first (round-based)
            applyRoundStartEffects(player, activeEnemies);

            if (allDefeated(activeEnemies) && config.hasBackup() && !backupSpawned) {
                List<Enemy> backup = config.getBackupWave();
                activeEnemies.addAll(backup);
                backupSpawned = true;

                ui.showMessage("== BACKUP SPAWN! Reinforcements arrive simultaneously! ==");
                for (Enemy e : backup) {
                    ui.showMessage("  + " + e.getName() + " joins the battle!");
                }
            }

            if (allDefeated(activeEnemies) && (!config.hasBackup() || backupSpawned)) {
                ui.showVictory(player, round);
                return true;
            }

            ui.showBattleState(player, activeEnemies, items);

            List<Combatant> turnOrder = buildTurnOrder(player, activeEnemies);

            for (Combatant combatant : turnOrder) {
                if (!combatant.isAlive()) {
                    continue;
                }

                if (combatant instanceof Player p) {
                    handlePlayerTurn(p, items, activeEnemies);
                } else if (combatant instanceof Enemy e) {
                    handleEnemyTurn(e, player);
                }

                if (!player.isAlive()) {
                    long remaining = activeEnemies.stream().filter(Enemy::isAlive).count();
                    ui.showDefeat((int) remaining, round);
                    return false;
                }

                if (allDefeated(activeEnemies)) {
                    if (config.hasBackup() && !backupSpawned) {
                        break;
                    } else {
                        ui.showVictory(player, round);
                        return true;
                    }
                }
            }

            // End-of-round ticking & expiration for round-based effects
            endRoundTick(player, activeEnemies);

            if (!player.isAlive()) {
                long remaining = activeEnemies.stream().filter(Enemy::isAlive).count();
                ui.showDefeat((int) remaining, round);
                return false;
            }
        }
    }

    private void handlePlayerTurn(Player player, List<Item> items, List<Enemy> activeEnemies) {
        player.tickCooldown();
        List<Enemy> aliveEnemies = getAliveEnemies(activeEnemies);

        if (!player.canAct()) {
            ui.showTurnSkipped(player);
            return;
        }

        while (true) {
            ActionChoice choice = ui.chooseAction(!items.isEmpty());

            switch (choice) {
                case BASIC_ATTACK -> {
                    Enemy target = ui.chooseTarget(aliveEnemies);

                    if (target == null) {
                        continue;
                    }

                    int damage = calcDamage(player, target);
                    target.takeDamage(damage);

                    ui.showMessage(player.getName() + " attacks " + target.getName()
                            + " for " + damage + " damage! HP: "
                            + target.getCurrentHp() + "/" + target.getMaxHp());

                    if (!target.isAlive()) {
                        ui.showMessage(target.getName() + " has been ELIMINATED!");
                    }

                    return;
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
                        continue;
                    }

                    int index = ui.chooseItemIndex(items);

                    if (index < 0 || index >= items.size()) {
                        ui.showMessage("Invalid item selection.");
                        continue;
                    }

                    Item item = items.get(index);
                    item.use(player, aliveEnemies, ui);
                    player.useItem(ItemType.valueOf(item.getType().name()));
                    items.remove(index);

                    ui.showMessage("Item used. Items remaining: " + items.size());
                    return;
                }

                case SPECIAL_SKILL -> {
                    if (!player.canUseSpecialSkill()) {
                        ui.showMessage("Special skill is on cooldown ("
                                + player.getCooldown() + " turn(s) remaining). Choose a different action.");
                        continue;
                    }

                    player.useSpecialSkill(aliveEnemies, ui);
                    return;
                }
            }
        }
    }

    private void handleEnemyTurn(Enemy enemy, Player player) {
        if (!enemy.canAct()) {
            ui.showTurnSkipped(enemy);
            return;
        }

        int damage = calcDamage(enemy, player);
        player.takeDamage(damage);

        ui.showMessage(enemy.getName() + " attacks " + player.getName()
                + " for " + damage + " damage! HP: "
                + player.getCurrentHp() + "/" + player.getMaxHp());
    }

    private List<Combatant> buildTurnOrder(Player player, List<Enemy> activeEnemies) {
        List<Combatant> turnOrder = new ArrayList<>();

        if (player.isAlive()) {
            turnOrder.add(player);
        }

        for (Enemy e : activeEnemies) {
            if (e.isAlive()) {
                turnOrder.add(e);
            }
        }

        turnOrder.sort(Comparator.comparingInt(Combatant::getSpeed).reversed());
        return turnOrder;
    }

    private int calcDamage(Combatant attacker, Combatant defender) {
        if (defender instanceof Player p && p.hasSmokeBomb()) {
            ui.showMessage("Smoke Bomb absorbs the attack! 0 damage.");
            return 0;
        }

        return Math.max(0, attacker.getAttack() - defender.getDefense());
    }

    private void applyRoundStartEffects(Player player, List<Enemy> activeEnemies) {
        if (player.isAlive()) {
            player.applyEffects();
        }

        for (Enemy enemy : activeEnemies) {
            if (enemy != null && enemy.isAlive()) {
                enemy.applyEffects();
            }
        }
    }

    private void endRoundTick(Player player, List<Enemy> activeEnemies) {
        if (player.isAlive()) {
            player.endRoundTickEffects();
        }

        for (Enemy enemy : activeEnemies) {
            if (enemy != null && enemy.isAlive()) {
                enemy.endRoundTickEffects();
            }
        }
    }

    private boolean allDefeated(List<Enemy> enemies) {
        return enemies.stream().noneMatch(Enemy::isAlive);
    }

    private List<Enemy> getAliveEnemies(List<Enemy> enemies) {
        List<Enemy> alive = new ArrayList<>();

        for (Enemy e : enemies) {
            if (e.isAlive()) {
                alive.add(e);
            }
        }

        return alive;
    }
}