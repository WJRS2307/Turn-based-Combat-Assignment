package arena.entity;

import arena.effect.Stun;

public class Warrior extends Player{

    public Warrior(String name, int currentHp, int attack, int defense, int speed) {
        super("Warrior", 260, 40, 20, 30);
    }
    
    @Override
    public void specialSkill(Combatant target){
        int dmg = Math.max(0, attack - target.getDefense());
        target.takeDamage(dmg);
        target.addEffect(new Stun(3));
        setSpecialSkillCooldown();
        System.out.println("Shield Bash! " + target.getName() + " stunned!");
    }
}
