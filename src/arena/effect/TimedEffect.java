package arena.effect;

import arena.entity.Combatant;

public abstract class TimedEffect implements StatusEffect{
    protected int duration;

    public TimedEffect(int duration){
        this.duration = duration;
    }

    @Override
    public void tick() {
        duration--;
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }
    
    @Override
    public void apply(Combatant target){}

    @Override public boolean canExecute(){
        return true;
    }
}
