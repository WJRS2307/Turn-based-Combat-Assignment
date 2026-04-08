package arena.item;

import arena.entity.Enemy;
import arena.entity.Player;
import arena.ui.GameUI;

import java.util.List;

public class Potion implements Item{
    private int value = 100;

    @Override
    public void use(Player player, List<Enemy> enemies, GameUI ui){
        player.heal(value);
        ui.showMessage("Potion used! +" + value + " HP");
    }

    @Override
    public ItemType getType() {
        return ItemType.POTION;
    }

}
