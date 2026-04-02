package arena.action;

import arena.entity.Combatant;
import arena.entity.Enemy;
import arena.entity.Player;
import arena.ui.GameUI;

import java.util.List;

public class SpecialSkill implements Action{

    @Override
    public void execute(Combatant actor, Combatant target, List<Enemy> enemies, GameUI ui) {
        if (!(actor instanceof Player)) return;

        Player player = (Player) actor;

        if (!player.canUseSpecialSkill()) {
            if (ui != null) {
                ui.showMessage("Skill is still on cooldown!");
            } else {
                System.out.println("Skill is still on cooldown!");
            }
            return;
        }

        player.useSpecialSkill(enemies, ui);
    }

}
