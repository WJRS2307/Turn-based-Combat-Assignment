package arena.entity;

import arena.effect.DefenseBuff;
import arena.effect.SmokeBombInvulnerability;
import arena.effect.StatusEffect;
import arena.item.Item;
import arena.item.ItemFactory;
import arena.item.ItemType;
import arena.ui.GameUI;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class Player extends Combatant{
    protected int specialSkillCooldown = 0;
    private final Map<ItemType,Integer> itemInventory = new EnumMap<>(ItemType.class);

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

    public void addItem(ItemType type) {
        itemInventory.put(type, itemInventory.getOrDefault(type, 0) + 1);
    }   

    public Map<ItemType, Integer> getItemInventory() {
        return itemInventory;
    }
    public boolean useItem(ItemType type){
        int count = itemInventory.get(type);

        if(count <= 0) return false;
        
        itemInventory.put(type, count -1);  
        return true;
    }
    public int getItemCount(ItemType Type) {
        return itemInventory.getOrDefault(Type,0);
    }

    public List<Item> getItemsAsList() {
        List<Item> items = new ArrayList<>();

        for (ItemType type : ItemType.values()) {
            int count = itemInventory.getOrDefault(type, 0);

            for (int i = 0; i < count; i++) {
                items.add(ItemFactory.create(type));
            }
        }

        return items;
    }
}
