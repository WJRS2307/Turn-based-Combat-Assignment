package arena.entity;

import arena.effect.DefenseBuff;
import arena.effect.SmokeBombInvulnerability;
import arena.effect.StatusEffect;
import arena.ui.GameUI;

import java.util.List;

public abstract class Player extends Combatant{
    protected int specialSkillCooldown = 0;

    public Player(String name, int currentHp, int attack, int defense, int speed) {
        super(name, currentHp, attack, defense, speed);
    }
    protected abstract void performSpecialSkill(List<Enemy> enemies, GameUI ui);

    public boolean canUseSpecialSkill(){
        return specialSkillCooldown == 0;
    }

    public int getCooldown() {
        return specialSkillCooldown;
    }

    public void tickCooldown() {
        if (this.specialSkillCooldown > 0) {
            this.specialSkillCooldown--;
        }
    }

    public void applyDefend() {
        DefenseBuff buff = new DefenseBuff(2);
        addEffect(buff);
        // Apply immediately so it affects the current round.
        buff.apply(this);
    }

    public boolean hasSmokeBomb() {
        for (StatusEffect effect : effects) {
            if (effect instanceof SmokeBombInvulnerability) {
                return true;
            }
        }
        return false;
    }

    public void applySmokeBomb() {
        addEffect(new SmokeBombInvulnerability(2));
    }

    public void useSpecialSkill(List<Enemy> enemies, GameUI ui) {
        if (!canUseSpecialSkill()) {
            return;
        }
        performSpecialSkill(enemies, ui);
        this.specialSkillCooldown = 3;
    }

    public void triggerSpecialSkillWithoutCooldown(List<Enemy> enemies, GameUI ui) {
        performSpecialSkill(enemies, ui);
    }
}
