package arena.item;

import arena.entity.Enemy;
import arena.entity.Player;
import arena.ui.GameUI;

import java.util.List;

public class SmokeBomb implements Item {

    @Override
    public void use(Player player, List<Enemy> enemies, GameUI ui) {
        player.applySmokeBomb();
        ui.showMessage("Smoke Bomb used! Enemy attacks deal 0 damage this turn and next.");
    }
}

