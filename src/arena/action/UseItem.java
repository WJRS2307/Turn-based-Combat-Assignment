package arena.action;

import arena.entity.Combatant;
import arena.entity.Player;
import arena.item.Item;

public class UseItem implements Action{

    private Item item;

    public UseItem(Item item){
        this.item = item;
    }

    @Override
    public void execute(Combatant actor, Combatant target) {
        if(!(actor instanceof Player)) return; 

        item.useItem(actor);
        System.out.println(actor.getName() + " used " + item.getClass().getSimpleName());
    }
    
}
