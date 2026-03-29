package arena.item;

import arena.entity.Combatant;

public class Potion implements Item{
    
    private int value = 100;

    @Override
    public void useItem(Combatant actor){
        actor.heal(value);
        System.out.println("Potion used! +100 HP");
    }
}
