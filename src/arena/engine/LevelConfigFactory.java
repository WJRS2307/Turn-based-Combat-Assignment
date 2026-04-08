package arena.engine;

import arena.entity.Enemy;
import arena.entity.Goblin;
import arena.entity.Wolf;

import java.util.ArrayList;
import java.util.List;

public final class LevelConfigFactory {
    private LevelConfigFactory() {}

    public static LevelConfig create(int difficulty) {
        List<Enemy> initial = new ArrayList<>();
        List<Enemy> backup = new ArrayList<>();

        switch (difficulty) {
            case 1 -> {
                initial.add(new Goblin('A'));
                initial.add(new Goblin('B'));
                initial.add(new Goblin('C'));
            }
            case 2 -> {
                initial.add(new Goblin('A'));
                initial.add(new Wolf('A'));
                backup.add(new Wolf('B'));
                backup.add(new Wolf('C'));
            }
            case 3 -> {
                initial.add(new Goblin('A'));
                initial.add(new Goblin('B'));
                backup.add(new Goblin('C'));
                backup.add(new Wolf('A'));
                backup.add(new Wolf('B'));
            }
            default -> throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
        }

        return new LevelConfig(difficulty, initial, backup);
        // BETTER because if i want to do it dynamically, i need to create functions like getInitialGoblinCount and getBackupGoblinCount and a function that adds a suffix to each enemy times the number of enemies
        // from a scalabilty perspective, this is better because if i want to add more difficulties, i just need to add more cases here and not create more functions. Also, the logic of how many enemies and what type of enemies are in each wave is all in one place, which makes it easier to understand and modify.
    }
}
