package arena.effect;

import arena.entity.Combatant;

public interface StatusEffect {

    void apply(Combatant target);
    boolean canExecute();
    void tick();
    boolean isExpired();

    default void onRemove(Combatant target) {
        // optional hook
    }
}
