package arena.effect;


public class StunEffect extends TimedEffect{

    public StunEffect(int duration) {
        super(duration);
    }

    @Override
    public boolean canExecute(){
        return false;
    }
}
