package arena.effect;

import arena.entity.Combatant;

public class DefenseBuff extends TimedEffect{
    private int defense = 10;
    private boolean isApplied = false;
    public DefenseBuff(int duration) {
        super(duration);
    }

    @Override
    public void apply(Combatant target){
        if(!isApplied){
            target.setDefense(target.getDefense() + defense);
            isApplied = true;
        }
    }

    @Override
    public void onRemove(Combatant target) {
        if (isApplied) {
            target.setDefense(target.getDefense() - defense);
        }
    }
}
