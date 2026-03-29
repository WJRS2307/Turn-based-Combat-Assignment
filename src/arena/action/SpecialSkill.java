package arena.action;

import arena.entity.Combatant;
import arena.entity.Player;

public class SpecialSkill implements Action{

    @Override
    public void execute(Combatant actor, Combatant target) {
        if (!(actor instanceof Player)) return;

        // Combantant cannot use special skill, must be a player obkect
        Player player = (Player) actor;

        if (!player.canUseSpecialSkill()) {
            System.out.println("Skill is still on cooldown!");
            return;
        }

        player.specialSkill(target);
    }

}
