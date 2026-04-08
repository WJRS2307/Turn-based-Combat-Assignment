package arena;

import arena.engine.BattleEngine;
import arena.engine.LevelConfig;
import arena.engine.LevelConfigFactory;
import arena.entity.Player;
import arena.entity.Warrior;
import arena.entity.Wizard;
import arena.item.Item;
import arena.item.ItemFactory;
import arena.ui.GameUI;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        GameUI ui = new GameUI();

        Integer lastPlayerChoice = null;
        List<Integer> lastItemChoices = null;
        Integer lastDifficulty = null;

        while (true) {
            ui.showLoadingScreen();

            int playerChoice;
            List<Integer> itemChoices = new ArrayList<>();
            int difficulty;

            if (lastPlayerChoice != null && lastItemChoices != null && lastDifficulty != null) {
                // If we have previous settings and user chose replay, we will reuse these.
                playerChoice = lastPlayerChoice;
                itemChoices.addAll(lastItemChoices);
                difficulty = lastDifficulty;
            } else {
                playerChoice = ui.choosePlayerType();
                itemChoices.add(ui.chooseStartingItem(1));
                itemChoices.add(ui.chooseStartingItem(2));
                difficulty = ui.chooseDifficulty();
            }

            Player player = (playerChoice == 1) ? new Warrior("Warrior", 260, 40, 20, 30) : new Wizard();

            for(int choice : itemChoices) {
                player.addItem(ItemFactory.fromChoice(choice));
            }

            List<Item> items = player.getItemsAsList(); // call so i dont have to keep calling
            LevelConfig config = LevelConfigFactory.create(difficulty);

            ui.showSetupSummary(player.getName(), items,difficulty);
            ui.pressEnterToContinue();

            BattleEngine engine = new BattleEngine(ui);
            engine.run(player, items, config);

            int post = ui.choosePostGameOption();
            if (post == 1) {
                lastPlayerChoice = playerChoice;
                lastItemChoices = itemChoices;
                lastDifficulty = difficulty;
                continue;
            }

            // Start a new game
            lastPlayerChoice = null;
            lastItemChoices = null;
            lastDifficulty = null;

            if (post == 3) return;
        }
    }
}