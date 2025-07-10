package me.AI.core.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.AI.core.game.entities.Enemy;
import me.AI.core.game.entities.Player;
import me.AI.core.game.entities.Projectile;
import me.AI.core.game.systems.EnemySpawner;
import me.AI.core.game.systems.ParticleManager;
import me.AI.core.game.systems.UpgradeSystem;
import me.AI.core.game.uI.GameUI;
import me.AI.core.game.uI.UpgradeUI;
import me.AI.core.game.utils.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private SpaceClickerGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;

    // Game objects
    private Player player;
    private EnemySpawner enemySpawner;
    private List<Projectile> projectiles;
    private ParticleManager particleManager;
    private UpgradeSystem upgradeSystem;
    private GameData gameData;

    // UI
    private GameUI gameUI;
    private UpgradeUI upgradeUI;

    // Constants
    private static final int WORLD_WIDTH = 800;
    private static final int WORLD_HEIGHT = 600;

    public GameScreen(SpaceClickerGame game) {
        this.game = game;
        this.batch = game.batch;

        // Create camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        // Initialize game systems
        gameData = new GameData();
        upgradeSystem = new UpgradeSystem(gameData);

        // Initialize game objects
        player = new Player(game.assets.playerShip, 50, WORLD_HEIGHT / 2);
        enemySpawner = new EnemySpawner(game.assets.enemyShip, gameData);
        projectiles = new ArrayList<>();
        particleManager = new ParticleManager(game.assets.star, game.assets.explosion);

        // Initialize UI
        gameUI = new GameUI(game.assets.font, game.assets.titleFont, gameData);
        upgradeUI = new UpgradeUI(game.assets.font, game.assets.titleFont, upgradeSystem, gameData);
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update game logic
        update(delta);

        // Set camera and begin batch
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Draw background
        batch.draw(game.assets.background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // Draw particles (background)
        particleManager.render(batch);

        // Draw player
        player.render(batch);

        // Draw enemies
        for (Enemy enemy : enemySpawner.getEnemies()) {
            enemy.render(batch);
        }

        // Draw projectiles
        for (Projectile projectile : projectiles) {
            projectile.render(batch);
        }

        // Draw UI
        gameUI.render(batch, player, enemySpawner);
        upgradeUI.render(batch, player);

        batch.end();
    }

    private void update(float delta) {
        // Handle input
        handleInput();

        // Update game objects
        player.update(delta);
        enemySpawner.update(delta);
        particleManager.update(delta);

        // Update projectiles
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.update(delta);

            if (projectile.isDestroyed()) {
                projectiles.remove(i);
            }
        }

        // Check collisions
        checkCollisions();

        // Enemy AI - attack player
        enemyAI();
    }

    private void handleInput() {
        // Click to shoot
        if (Gdx.input.justTouched()) {
            fireProjectile();
        }

        // Upgrade menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            upgradeUI.toggle();
        }

        // Handle upgrades
        if (upgradeUI.isVisible()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                if (upgradeSystem.purchaseUpgrade("damage", player)) {
                    game.assets.upgradeSound.play(0.5f);
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                if (upgradeSystem.purchaseUpgrade("health", player)) {
                    game.assets.upgradeSound.play(0.5f);
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                if (upgradeSystem.purchaseUpgrade("speed", player)) {
                    game.assets.upgradeSound.play(0.5f);
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
                if (upgradeSystem.purchaseUpgrade("firerate", player)) {
                    game.assets.upgradeSound.play(0.5f);
                }
            }
        }

        // Reset save data
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            gameData.resetData();
        }

        // Exit game
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void fireProjectile() {
        if (player.canFire()) {
            player.fire();
            gameData.incrementClickCount();

            // Create projectile
            float x = player.getPosition().x + 60; // Offset from player
            float y = player.getPosition().y + 30;
            Projectile projectile = new Projectile(game.assets.laser, x, y, player.getDamage());
            projectiles.add(projectile);

            // Play sound
            game.assets.laserSound.play(0.3f);
        }
    }

    private void checkCollisions() {
        // Projectile vs Enemy collision
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);

            for (Enemy enemy : enemySpawner.getEnemies()) {
                if (projectile.checkCollision(enemy)) {
                    enemy.takeDamage(projectile.getDamage());
                    projectile.destroy();

                    // Create explosion effect
                    particleManager.createExplosion(enemy.getPosition().x, enemy.getPosition().y);

                    if (enemy.isDestroyed()) {
                        gameData.addMoney(enemy.getType().reward);
                        gameData.incrementEnemiesKilled();
                        game.assets.explosionSound.play(0.4f);
                    }

                    break;
                }
            }
        }
    }

    private void enemyAI() {
        for (Enemy enemy : enemySpawner.getEnemies()) {
            // Check if enemy is close enough to attack
            float distance = enemy.getPosition().dst(player.getPosition());
            if (distance < 100 && enemy.canAttack()) {
                enemy.attack();
                player.takeDamage(enemy.getType().damage);

                // Create damage effect
                particleManager.createExplosion(player.getPosition().x, player.getPosition().y);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        upgradeUI.dispose();
    }
}
