package me.darkinfect.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;

import java.awt.*;
import java.util.ArrayList;

public class BossFightScene implements Screen {
    private SpriteBatch batch;
    private Texture playerTexture, bossTexture, projectileTexture;
    private Rectangle player, boss;
    private ArrayList<Rectangle> playerProjectiles, bossProjectiles;
    private ArrayList<Rectangle> minions;
    private int playerLevel = 1;
    private int bossHealth = 100;
    private int minionsDefeated = 0;
    private final int MINIONS_TO_DEFEAT = 4;
    private boolean isBossActive = false;

    public BossFightScene() {
        batch = new SpriteBatch();
        playerTexture = new Texture("ship.png");
        bossTexture = new Texture("boss.jpg");
        projectileTexture = new Texture("bullet.jpg");

        // Инициализация игрока и босса
        player = new Rectangle(
                Gdx.graphics.getWidth() / 2f - 32,
                50,
                64, 64
        );
        boss = new Rectangle(
                Gdx.graphics.getWidth() / 2f - 64,
                Gdx.graphics.getHeight() - 100,
                128, 128
        );

        playerProjectiles = new ArrayList<>();
        bossProjectiles = new ArrayList<>();
        minions = new ArrayList<>();

        spawnMinions();
    }

    private void spawnMinions() {
        minions.clear();
        for (int i = 0; i < MINIONS_TO_DEFEAT; i++) {
            minions.add(new Rectangle(
                    i * 150 + 50,
                    Gdx.graphics.getHeight() - 150,
                    80, 80
            ));
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        // Отрисовка
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        // Рисуем игрока и босса/миньонов
        batch.draw(playerTexture, player.x, player.y, player.width, player.height);

        if (isBossActive) {
            batch.draw(bossTexture, boss.x, boss.y, boss.width, boss.height);
        } else {
            for (Rectangle minion : minions) {
                batch.draw(bossTexture, minion.x, minion.y, minion.width, minion.height);
            }
        }

        // Пули игрока
        for (Rectangle projectile : playerProjectiles) {
            batch.draw(projectileTexture, projectile.x, projectile.y, 16, 16);
        }

        // Пули босса
        for (Rectangle projectile : bossProjectiles) {
            batch.draw(projectileTexture, projectile.x, projectile.y, 16, 16);
        }

        batch.end();
    }

    private void update(float delta) {
        handleInput();
        updateProjectiles();
        updateBossAI();
        checkCollisions();
    }

    private void handleInput() {
        // Движение игрока
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.x -= 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.x += 200 * Gdx.graphics.getDeltaTime();
        }

        // Стрельба (Пробел)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            playerProjectiles.add(new Rectangle(
                    player.x + player.width / 2 - 8,
                    player.y + player.height,
                    16, 16
            ));
        }
    }

    private void updateProjectiles() {
        // Пули игрока
        Iterator<Rectangle> iter = playerProjectiles.iterator();
        while (iter.hasNext()) {
            Rectangle projectile = iter.next();
            projectile.y += 400 * Gdx.graphics.getDeltaTime();

            if (projectile.y > Gdx.graphics.getHeight()) {
                iter.remove();
            }
        }

        // Пули босса
        iter = bossProjectiles.iterator();
        while (iter.hasNext()) {
            Rectangle projectile = iter.next();
            projectile.y -= 300 * Gdx.graphics.getDeltaTime();

            if (projectile.y < 0) {
                iter.remove();
            }
        }
    }

    private void updateBossAI() {
        if (!isBossActive) {
            // Атака миньонов
            if (Math.random() < 0.02) {
                for (Rectangle minion : minions) {
                    bossProjectiles.add(new Rectangle(
                            minion.x + minion.width / 2 - 8,
                            minion.y,
                            16, 16
                    ));
                }
            }
            return;
        }

        // Атака босса
        if (Math.random() < 0.01) {
            bossProjectiles.add(new Rectangle(
                    boss.x + boss.width / 2 - 8,
                    boss.y,
                    16, 16
            ));
        }
    }

    private void checkCollisions() {
        // Проверка попаданий игрока по миньонам/боссу
        Iterator<Rectangle> projectileIter = playerProjectiles.iterator();
        while (projectileIter.hasNext()) {
            Rectangle projectile = projectileIter.next();

            if (!isBossActive) {
                // Столкновения с миньонами
                Iterator<Rectangle> minionIter = minions.iterator();
                while (minionIter.hasNext()) {
                    Rectangle minion = minionIter.next();
                    if (projectile.overlaps(minion)) {
                        minionIter.remove();
                        projectileIter.remove();
                        minionsDefeated++;

                        if (minionsDefeated >= MINIONS_TO_DEFEAT) {
                            isBossActive = true;
                        }
                        break;
                    }
                }
            } else {
                // Столкновения с боссом
                if (projectile.overlaps(boss)) {
                    bossHealth -= 5 * playerLevel; // Урон зависит от уровня
                    projectileIter.remove();

                    if (bossHealth <= 0) {
                        // Победа!
                        Gdx.app.log("BossFight", "BOSS DEFEATED!");
                    }
                }
            }
        }

        // Проверка попаданий босса по игроку
        for (Rectangle projectile : bossProjectiles) {
            if (projectile.overlaps(player)) {
                // Урон игроку
                Gdx.app.log("BossFight", "Player hit!");
                break;
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        playerTexture.dispose();
        bossTexture.dispose();
        projectileTexture.dispose();
    }

    // Остальные методы Screen...
    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
