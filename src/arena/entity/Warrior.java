package arena.entity;

import arena.effect.Stun;
import arena.ui.GameUI;

import java.util.List;

public class Warrior extends Player{

    public Warrior(String name, int currentHp, int attack, int defense, int speed) {
        super("Warrior", 260, 40, 20, 30);
    }
    
    @Override
    protected void performSpecialSkill(List<Enemy> enemies, GameUI ui){
        Enemy target = ui.chooseTarget(enemies);
        if (target == null) {
            ui.showMessage("No valid target.");
            return;
        }

        int dmg = Math.max(0, attack - target.getDefense());
        target.takeDamage(dmg);
        target.addEffect(new Stun(2));

        ui.showMessage("Shield Bash! " + target.getName() + " takes " + dmg + " damage and is STUNNED!");
        if (!target.isAlive()) {
            ui.showMessage(target.getName() + " has been ELIMINATED!");
        }
    }
}
