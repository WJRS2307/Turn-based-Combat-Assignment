package arena.entity;

public abstract class Player extends Combatant{
    protected int specialSkillCooldown = 0;

    public Player(String name, int currentHp, int attack, int defense, int speed) {
        super(name, currentHp, attack, defense, speed);
    }
    public abstract void specialSkill(Combatant target);

    public boolean canUseSpecialSkill(){
        return specialSkillCooldown == 0;
    }

    public void setSpecialSkillCooldown(){
        this.specialSkillCooldown = 3;
    }

    public void reduceSpecialSkillCooldown(){
        if(this.specialSkillCooldown > 0)
            this.specialSkillCooldown--;
    }

}
