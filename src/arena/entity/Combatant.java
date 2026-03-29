package arena.entity;

import java.util.ArrayList;
import java.util.List;
import arena.effect.StatusEffect;

public abstract class Combatant {
    protected String name;
    protected int currentHp, maxHp, attack, defense, speed;
    protected List<StatusEffect> effects = new ArrayList<>();

    public Combatant(String name, int currentHp, int attack, int defense, int speed){
        this.name = name;
        this.currentHp = currentHp;
        this.maxHp = currentHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }

    // Default getters
    public int getAttack() { return this.attack; }
    public int getDefense() { return this.defense; }
    public int getSpeed() { return this.speed; }
    public String getName() { return this.name; }
    public int getCurrentHp() { return this.currentHp; }
    public void setDefense(int def){ this.defense = def;}

    public boolean isAlive(){
        return this.currentHp > 0;
    }

    // Ensure hp will never be < 0
    public void takeDamage(int dmg){
        this.currentHp = Math.max(0, currentHp - dmg);
    }
    // Ensure hp doesnt > maxHp
    public void heal(int amount) {
        this.currentHp = Math.min(maxHp, this.currentHp + amount);
    }

    public void addEffect(StatusEffect effect) {
        effects.add(effect);
    }
    public List<StatusEffect> getEffects(){
        return this.effects;
    }
}
