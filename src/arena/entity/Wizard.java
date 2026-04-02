package arena.entity;

import arena.ui.GameUI;

import java.util.List;

public class Wizard extends Player{
    public Wizard() {
        super("Wizard", 200, 50, 10, 20);
    }

    @Override
    protected void performSpecialSkill(List<Enemy> enemies, GameUI ui){
        int kills = 0;
        ui.showMessage("Arcane Blast hits all enemies!");

        for (Enemy enemy : enemies) {
            if (enemy == null || !enemy.isAlive()) {
                continue;
            }

            int dmg = Math.max(0, this.attack - enemy.getDefense());
            enemy.takeDamage(dmg);
            ui.showMessage("  " + enemy.getName() + " takes " + dmg + " damage! HP: "
                    + enemy.getCurrentHp() + "/" + enemy.getMaxHp());

            if (!enemy.isAlive()) {
                kills++;
                ui.showMessage("  " + enemy.getName() + " has been ELIMINATED!");
            }
        }

        if (kills > 0) {
            this.attack += 10 * kills;
            ui.showMessage("Arcane Surge! Wizard ATK increases by " + (10 * kills) + " until end of level.");
        }
    }
}
