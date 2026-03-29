package arena.entity;

public abstract class Enemy extends Combatant{

    public Enemy(String name, int currentHp, int attack, int defense, int speed) {
        super(name, currentHp, attack, defense, speed);
    }
    

}
