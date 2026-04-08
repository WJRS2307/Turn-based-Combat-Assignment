package arena.item;
import arena.entity.Enemy;
import arena.entity.Player;
import arena.ui.GameUI;

import java.util.List;

public interface Item {
    void use(Player player, List<Enemy> enemies, GameUI ui);
    ItemType getType();
}
