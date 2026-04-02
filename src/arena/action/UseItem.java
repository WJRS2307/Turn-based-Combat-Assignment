package arena.action;

import arena.entity.Combatant;
import arena.entity.Enemy;
import arena.entity.Player;
import arena.item.Item;
import arena.ui.GameUI;

import java.util.List;

public class UseItem implements Action{

    private Item item;

    public UseItem(Item item){
        this.item = item;
    }

    @Override
    public void execute(Combatant actor, Combatant target, List<Enemy> enemies, GameUI ui) {
        if(!(actor instanceof Player)) return; 

        item.use((Player) actor, enemies, ui);
        if (ui != null) {
            ui.showMessage(actor.getName() + " used " + item.getClass().getSimpleName());
        } else {
            System.out.println(actor.getName() + " used " + item.getClass().getSimpleName());
        }
    }
    
}
