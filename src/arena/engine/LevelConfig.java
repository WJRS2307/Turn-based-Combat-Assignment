package arena.battle;

import arena.entity.Enemy;
import java.util.List;

public class LevelConfig {
	
	private final int difficulty;
	private final List<Enemy> initialWave;
	private final List<Enemy> backupWave;
	
	public LevelConfig(int difficulty, List<Enemy> initialWave, List<Enemy> backupWave) {
		this.difficulty = difficulty;
		this.initialWave = initialWave;
		this.backupWave = backupWave;
	}
	
	public List<Enemy> getInitialWave(){
		return List.copyOf(initialWave);
	}
	
	public List<Enemy> getBackupWave(){
		return List.copyOf(backupWave);
	}
	
	public boolean hasBackup() {
		return !backupWave.isEmpty();
	}
	
	public int getDifficulty() {
		return difficulty;
	}

}
