package arena.action;

import arena.effect.DefenseBuff;
import arena.entity.Combatant;

public class Defend implements Action{

    @Override
    public void execute(Combatant actor, Combatant target, java.util.List<arena.entity.Enemy> enemies, arena.ui.GameUI ui) {
        actor.addEffect(new DefenseBuff(2));
        if (ui != null) {
            ui.showMessage(actor.getName() + " defends! (+10 DEF for 2 turns)");
        } else {
            System.out.println(actor.getName() + " defends! (+10 DEF for 2 turns)");
        }
    }
}
