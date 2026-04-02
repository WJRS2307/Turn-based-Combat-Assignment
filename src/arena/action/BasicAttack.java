package arena.action;

import arena.entity.Combatant;

public class BasicAttack implements Action{

    @Override
    public void execute(Combatant user, Combatant target, java.util.List<arena.entity.Enemy> enemies, arena.ui.GameUI ui) {
        int dmg = Math.max(0, user.getAttack() - target.getDefense());
        target.takeDamage(dmg);
        if (ui != null) {
            ui.showMessage(user.getName() + " attacks " + target.getName() + " for " + dmg);
        } else {
            System.out.println(user.getName() + " attacks " + target.getName() + " for " + dmg);
        }
    }
    
}
