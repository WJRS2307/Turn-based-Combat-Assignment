package arena.action;

import arena.entity.Combatant;

public interface Action {
    void execute(Combatant actor, Combatant target);
}