package me.AI.core.game.systems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import me.AI.core.game.entities.Enemy;
import me.AI.core.game.utils.GameData;

import java.util.ArrayList;
import java.util.List;

public class EnemySpawner {
    private List<Enemy> enemies;
    private Texture enemyTexture;
    private float spawnTimer;
    private float spawnInterval;
    private GameData gameData;
    private int wave;

    public EnemySpawner(Texture enemyTexture, GameData gameData) {
        this.enemyTexture = enemyTexture;
        this.gameData = gameData;
        this.enemies = new ArrayList<>();
        this.spawnInterval = 2.0f;
        this.spawnTimer = 0;
        this.wave = 1;
    }

    public void update(float delta) {
        spawnTimer += delta;

        if (spawnTimer >= spawnInterval) {
            spawnEnemy();
            spawnTimer = 0;

            // Increase difficulty over time
            if (gameData.getEnemiesKilled() > 0 && gameData.getEnemiesKilled() % 10 == 0) {
                wave++;
                spawnInterval = Math.max(0.5f, spawnInterval - 0.1f);
            }
        }

        // Update all enemies
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(delta);

            if (enemy.isDestroyed()) {
                enemies.remove(i);
            }
        }
    }

    private void spawnEnemy() {
        float screenWidth = 800; // Adjust based on your screen width
        float screenHeight = 600; // Adjust based on your screen height

        // Spawn at random Y position on the right side
        float y = MathUtils.random(50, screenHeight - 100);

        // Choose enemy type based on wave
        Enemy.EnemyType type = chooseEnemyType();

        Enemy enemy = new Enemy(enemyTexture, type, screenWidth, y);
        enemies.add(enemy);
    }

    private Enemy.EnemyType chooseEnemyType() {
        float random = MathUtils.random();

        if (wave < 3) {
            return Enemy.EnemyType.SCOUT;
        } else if (wave < 6) {
            return random < 0.7f ? Enemy.EnemyType.SCOUT : Enemy.EnemyType.FIGHTER;
        } else if (wave < 10) {
            if (random < 0.5f) return Enemy.EnemyType.SCOUT;
            else if (random < 0.8f) return Enemy.EnemyType.FIGHTER;
            else return Enemy.EnemyType.CRUISER;
        } else {
            if (random < 0.3f) return Enemy.EnemyType.SCOUT;
            else if (random < 0.6f) return Enemy.EnemyType.FIGHTER;
            else if (random < 0.85f) return Enemy.EnemyType.CRUISER;
            else return Enemy.EnemyType.BATTLESHIP;
        }
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public int getWave() {
        return wave;
    }
}
