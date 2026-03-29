package arena.effect;

import arena.entity.Combatant;

public class Stun extends TimedEffect{

    public Stun(int duration) {
        super(duration);
    }

    @Override
    public boolean canExecute(){
        return false;
    }
}
