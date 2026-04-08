    package arena.ui;

    import arena.effect.StatusEffect;
    import arena.entity.Combatant;
    import arena.entity.Enemy;
    import arena.entity.Player;
    import arena.item.Item;
    import arena.item.ItemType;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;
    import java.util.Scanner;

        public class GameUI {

            private final Scanner scanner = new Scanner(System.in);

            public enum ActionChoice {
                BASIC_ATTACK,
                DEFEND,
                USE_ITEM,
                SPECIAL_SKILL
            }

            public void showLoadingScreen() {
                println("========================================");
                println("SC2002 Turn-Based Combat Arena");
                println("========================================");
                println("Players:");
                println("1. Warrior - HP 260, ATK 40, DEF 20, SPD 30, Skill: Shield Bash");
                println("2. Wizard  - HP 200, ATK 50, DEF 10, SPD 20, Skill: Arcane Blast");
                println("");
                println("Items:");
                println("1. Potion - Heal 100 HP");
                println("2. Power Stone - Trigger special skill once without changing cooldown");
                println("3. Smoke Bomb - Enemy attacks deal 0 damage this turn and next turn");
                println("");
                println("Difficulties:");
                println("1. Easy   - 3 Goblins");
                println("2. Medium - 1 Goblin + 1 Wolf, then 2 Wolves backup");
                println("3. Hard   - 2 Goblins, then 1 Goblin + 2 Wolves backup");
                println("========================================");
            }

            public int choosePlayerType() {
                println("Choose your player:");
                println("1. Warrior");
                println("2. Wizard");
                return readInt("Enter choice: ", 1, 2);
            }

            public int chooseDifficulty() {
                println("Choose difficulty:");
                println("1. Easy");
                println("2. Medium");
                println("3. Hard");
                return readInt("Enter choice: ", 1, 3);
            }

            public int chooseStartingItem(int pickNumber) {
                println("Choose item #" + pickNumber + ":");
                println("1. Potion");
                println("2. Power Stone");
                println("3. Smoke Bomb");
                return readInt("Item choice: ", 1, 3);
            }

            public void showSetupSummary(String playerName, List<Item> items, int difficulty) {
                println("");
                println("========== GAME SETUP ==========");
                println("Player: " + playerName);
                println("Items: " + formatItems(items));
                println("Difficulty: " + difficultyName(difficulty));
                println("===============================");
                println("");
            }

            public void showRoundHeader(int roundNumber) {
                println("");
                println("----------------------------------------");
                println("Round " + roundNumber);
                println("----------------------------------------");
            }

            public void showBattleState(Player player, List<Enemy> enemies, List<Item> items) {
                println("");
                println("========== BATTLE STATUS ==========");
                println("Player:");
                println(player.getName()
                        + " | HP: " + player.getCurrentHp()
                        + " | ATK: " + player.getAttack()
                        + " | DEF: " + player.getDefense()
                        + " | SPD: " + player.getSpeed()
                        + " | Skill Ready: " + (player.canUseSpecialSkill() ? "Yes" : "No")
                        + " | Effects: " + formatEffects(player));

                println("Items: " + formatItems(items));

                println("");
                println("Enemies:");
                if (enemies.isEmpty()) {
                    println("None");
                } else {
                    for (int i = 0; i < enemies.size(); i++) {
                        Enemy enemy = enemies.get(i);
                        println((i + 1) + ". "
                                + enemy.getName()
                                + " | HP: " + enemy.getCurrentHp()
                                + " | ATK: " + enemy.getAttack()
                                + " | DEF: " + enemy.getDefense()
                                + " | SPD: " + enemy.getSpeed()
                                + " | Status: " + (enemy.isAlive() ? "Alive" : "Eliminated")
                                + " | Effects: " + formatEffects(enemy));
                    }
                }
                println("==================================");
                println("");
            }

            public ActionChoice chooseAction(boolean hasItems) {
                println("Choose an action:");
                println("1. BasicAttack");
                println("2. Defend");
                println("3. Use Item" + (hasItems ? "" : " (No items available)"));
                println("4. SpecialSkill");

                int choice = readInt("Action: ", 1, 4);

                return switch (choice) {
                    case 1 -> ActionChoice.BASIC_ATTACK;
                    case 2 -> ActionChoice.DEFEND;
                    case 3 -> ActionChoice.USE_ITEM;
                    default -> ActionChoice.SPECIAL_SKILL;
                };
            }

            public Enemy chooseTarget(List<Enemy> enemies) {
                List<Enemy> aliveEnemies = getAliveEnemies(enemies);

                if (aliveEnemies.isEmpty()) {
                    println("There are no valid targets.");
                    return null;
                }

                println("Choose a target:");
                for (int i = 0; i < aliveEnemies.size(); i++) {
                    Enemy enemy = aliveEnemies.get(i);
                    println((i + 1) + ". "
                            + enemy.getName()
                            + " | HP: " + enemy.getCurrentHp()
                            + " | Effects: " + formatEffects(enemy));
                }

                int choice = readInt("Target: ", 1, aliveEnemies.size());
                return aliveEnemies.get(choice - 1);
            }

            public int chooseItemIndex(List<Item> items) {
                if (items == null || items.isEmpty()) {
                    println("No items available.");
                    return -1;
                }

                println("Choose an item:");
                for (int i = 0; i < items.size(); i++) {
                    println((i + 1) + ". " + itemName(items.get(i)));
                }

                return readInt("Item: ", 1, items.size()) - 1;
            }

            public void showMessage(String message) {
                println(message);
            }

            public void showTurnSkipped(Combatant combatant) {
                println(combatant.getName() + " cannot act this turn.");
            }

            public void showVictory(Player player, int totalRounds) {
                println("");
                println("Victory!");
                println("Congratulations, you have defeated all your enemies.");
                println("Remaining HP: " + player.getCurrentHp());
                println("Final Attack: " + player.getAttack());
                println("Total Rounds: " + totalRounds);
                println(formatInventory(player.getItemInventory(),player));
                println("");
            }
            
            public String formatInventory(Map<ItemType,Integer> items, Player player) {
                StringBuilder sb = new StringBuilder();
                boolean first = true;

                for (ItemType type : ItemType.values()) {
                    if (!first) {
                        sb.append(" | ");
                    }

                    int count = player.getItemCount(type);

                    sb.append("Remaining ")
                    .append(type.getDisplayName())
                    .append(": ")
                    .append(count);

                    if (count > 0) {
                        sb.append(" <- unused");
                    }

                    first = false;
                }

                return sb.toString();
            }

            public void showDefeat(int enemiesRemaining, int totalRounds) {
                println("");
                println("Defeat!");
                println("Don't give up, try again!");
                println("Enemies remaining: " + enemiesRemaining);
                println("Total Rounds Survived: " + totalRounds);
                println("");
            }

            public int choosePostGameOption() {
                println("1. Replay with same settings");
                println("2. Start a new game");
                println("3. Exit");
                return readInt("Choice: ", 1, 3);
            }

            public void pressEnterToContinue() {
                println("Press Enter to continue...");
                scanner.nextLine();
            }

            private int readInt(String prompt, int min, int max) {
                while (true) {
                    System.out.print(prompt);
                    String input = scanner.nextLine().trim();

                    try {
                        int value = Integer.parseInt(input);
                        if (value >= min && value <= max) {
                            return value;
                        }
                    } catch (NumberFormatException ignored) {
                    }

                    println("Invalid input. Please enter a number between " + min + " and " + max + ".");
                }
            }

            private List<Enemy> getAliveEnemies(List<Enemy> enemies) {
                List<Enemy> alive = new ArrayList<>();
                if (enemies == null) {
                    return alive;
                }

                for (Enemy enemy : enemies) {
                    if (enemy != null && enemy.isAlive()) {
                        alive.add(enemy);
                    }
                }
                return alive;
            }

            private String formatItems(List<Item> items) {
                if (items == null || items.isEmpty()) {
                    return "None";
                }

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < items.size(); i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(itemName(items.get(i)));
                }
                return sb.toString();
            }

            private String itemName(Item item) {
                if (item == null) {
                    return "Unknown";
                }
                return item.getClass().getSimpleName();
            }

            private String formatEffects(Combatant combatant) {
                List<StatusEffect> effects = combatant.getEffects();
                if (effects == null || effects.isEmpty()) {
                    return "None";
                }

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < effects.size(); i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(effects.get(i).getClass().getSimpleName());
                }
                return sb.toString();
            }

            private String difficultyName(int difficulty) {
                return switch (difficulty) {
                    case 1 -> "Easy";
                    case 2 -> "Medium";
                    case 3 -> "Hard";
                    default -> "Unknown";
                };
            }

            private void println(String message) {
                System.out.println(message);
            }
        }
