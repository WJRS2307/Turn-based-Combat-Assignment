package arena.item;

import arena.entity.Enemy;
import arena.entity.Player;
import arena.ui.GameUI;

import java.util.List;

public class PowerStone implements Item {

    @Override
    public void use(Player player, List<Enemy> enemies, GameUI ui) {
        ui.showMessage("Power Stone used! Special skill triggers once without changing cooldown.");
        player.triggerSpecialSkillWithoutCooldown(enemies, ui);
    }

    @Override
    public ItemType getType() {
        return ItemType.POWER_STONE;
    }
}

