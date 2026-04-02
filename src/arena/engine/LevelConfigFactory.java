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
                initial.add(new Goblin());
                initial.add(new Goblin());
                initial.add(new Goblin());
            }
            case 2 -> {
                initial.add(new Goblin());
                initial.add(new Wolf());
                backup.add(new Wolf());
                backup.add(new Wolf());
            }
            case 3 -> {
                initial.add(new Goblin());
                initial.add(new Goblin());
                backup.add(new Goblin());
                backup.add(new Wolf());
                backup.add(new Wolf());
            }
            default -> throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
        }

        return new LevelConfig(difficulty, initial, backup);
    }
}
