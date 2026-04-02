package arena.action;

import arena.entity.Combatant;
import arena.entity.Enemy;
import arena.ui.GameUI;

import java.util.List;

public interface Action {
    void execute(Combatant actor, Combatant target, List<Enemy> enemies, GameUI ui);
}