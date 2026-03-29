package arena.action;

import arena.effect.DefenseBuff;
import arena.entity.Combatant;

public class Defend implements Action{

    @Override
    public void execute(Combatant actor, Combatant target) {
        actor.addEffect(new DefenseBuff(2));
        System.out.println(actor.getName() + " defends! (+10 DEF for 2 turns)");
    }
}
