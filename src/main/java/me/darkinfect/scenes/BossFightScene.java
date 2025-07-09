package me.darkinfect.scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import me.darkinfect.Main;

import java.util.ArrayList;
import java.util.Iterator;

import java.awt.*;
import java.util.ArrayList;

public class BossFightScene implements Screen {
    private Game game;
    private SpriteBatch batch;
    private Texture playerTexture, bossTexture, projectileTexture, background;
    private Rectangle player, boss;
    private ArrayList<Rectangle> playerProjectiles, bossProjectiles;
    private ArrayList<Rectangle> minions;
    // Константы скоростей
    private static final float PLAYER_PROJECTILE_SPEED = 400f;
    private static final float BOSS_PROJECTILE_SPEED = 300f; // Общая скорость снарядов босса
    private static int playerLevel = 1;
    private int bossHealth = 100;
    private int minionsDefeated = 0;
    private final int MINIONS_TO_DEFEAT = 4;
    private boolean isBossActive = false;
    public static void addplayerlevel(int value){
        playerLevel+=value;
        MainScene.getIntsance1().updateLabelCoin();
    }

    public BossFightScene(Game game) {
        batch = new SpriteBatch();
        playerTexture = new Texture("ship.png");
        bossTexture = new Texture("boss.jpg");
        projectileTexture = new Texture("bullet.jpg");
        background = new Texture("backgroundboss.jpg");


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
        this.game= game;
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
        batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
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
                        MainScene.addCoin(100);
                        MainScene.getIntsance1().updateLabelCoin();
                        game.setScreen(MainScene.getIntsance(game));
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
    private void updateBossAI() {
        if (!isBossActive) {
            updateMinionAttacks();
            return;
        }

        // Новые типы атак босса
        float time = Gdx.graphics.getFrameId() * 0.016f; // Примерное время в секундах

        // 1. Спиральная атака (активна всегда)
        if (time % 0.2f < 0.016f) { // Каждые 0.2 секунды
            float angle = time * 180f; // Угол увеличивается со временем
            createSpiralProjectile(angle);
        }

        // 2. Фазовая атака (меняется каждые 5 секунд)
        int attackPhase = ((int)(time / 5f)) % 4;

        switch (attackPhase) {
            case 0: // Веерная атака
                if (time % 1f < 0.016f) {
                    createFanAttack(5, 60f);
                }
                break;

            case 1: // Прицельная атака
                if (time % 0.7f < 0.016f) {
                    createTargetedAttack(3);
                }
                break;

            case 2: // Круговой залп
                if (time % 2f < 0.016f) {
                    createCircleAttack(12);
                }
                break;

            case 3: // Волновая атака
                if (time % 0.4f < 0.016f) {
                    createWaveAttack((int)(time * 2f) % 5);
                }
                break;
        }
    }

    private void updateMinionAttacks() {
        if (Math.random() < 0.02) {
            for (Rectangle minion : minions) {
                bossProjectiles.add(new Rectangle(
                        minion.x + minion.width / 2 - 8,
                        minion.y,
                        16, 16
                ));
            }
        }
    }

// Новые методы атак:

    private void createSpiralProjectile(float angle) {
        float rad = (float)Math.toRadians(angle);
        float speed = 200f;
        bossProjectiles.add(createProjectile(
                boss.x + boss.width/2 - 8,
                boss.y,
                (float)Math.sin(rad) * speed,
                (float)Math.cos(rad) * -speed
        ));
    }

    private void createFanAttack(int count, float spread) {
        float startAngle = -spread/2;
        float angleStep = spread/(count-1);

        for (int i = 0; i < count; i++) {
            float angle = startAngle + angleStep*i;
            bossProjectiles.add(new Rectangle(
                    boss.x + boss.width/2 - 8,
                    boss.y,
                    16, 16
            ));
            // Направление сохраняется через угол, но скорость постоянная
        }
    }

    private void createTargetedAttack(int count) {
        for (int i = 0; i < count; i++) {
            // Добавляем небольшой разброс
            float offsetX = (float)Math.random() * 40 - 20;
            float offsetY = (float)Math.random() * 40 - 20;

            // Вектор к игроку
            float dx = (player.x + player.width/2 + offsetX) - (boss.x + boss.width/2);
            float dy = (player.y + player.height/2 + offsetY) - boss.y;
            float len = (float)Math.sqrt(dx*dx + dy*dy);

            if (len > 0) {
                float speed = 300f;
                bossProjectiles.add(createProjectile(
                        boss.x + boss.width/2 - 8,
                        boss.y,
                        dx/len * speed,
                        dy/len * speed
                ));
            }
        }
    }

    private void createCircleAttack(int count) {
        float angleStep = 360f/count;
        float speed = 180f;

        for (int i = 0; i < count; i++) {
            float angle = i * angleStep;
            float rad = (float)Math.toRadians(angle);
            bossProjectiles.add(createProjectile(
                    boss.x + boss.width/2 - 8,
                    boss.y,
                    (float)Math.sin(rad) * speed,
                    (float)Math.cos(rad) * -speed
            ));
        }
    }

    private void createWaveAttack(int waveType) {
        float speed = 220f;
        int waves = 3 + waveType;

        for (int i = 0; i < waves; i++) {
            float offset = i * 30f;
            // Вертикальные волны
            bossProjectiles.add(createProjectile(
                    boss.x + boss.width/2 - 8 + offset,
                    boss.y,
                    0,
                    -speed
            ));
            // Горизонтальные волны
            bossProjectiles.add(createProjectile(
                    boss.x + boss.width/2 - 8,
                    boss.y - offset,
                    speed,
                    0
            ));
        }
    }

    private Rectangle createProjectile(float x, float y, float velX, float velY) {
        Rectangle projectile = new Rectangle(x, y, 16, 16);
//        projectile.setUserObject(new Vector2(velX, velY));
        return projectile;
    }

    // Обновленный метод updateProjectiles():
    private void updateProjectiles() {
        // Пули игрока
        Iterator<Rectangle> iter = playerProjectiles.iterator();
        while (iter.hasNext()) {
            Rectangle projectile = iter.next();
            projectile.y += PLAYER_PROJECTILE_SPEED * Gdx.graphics.getDeltaTime();
            if (projectile.y > Gdx.graphics.getHeight()) iter.remove();
        }

        // Пули босса (теперь с постоянной скоростью)
        iter = bossProjectiles.iterator();
        while (iter.hasNext()) {
            Rectangle projectile = iter.next();
            projectile.y -= BOSS_PROJECTILE_SPEED * Gdx.graphics.getDeltaTime(); // Все летят вниз с одинаковой скоростью

            if (projectile.y < 0) {
                iter.remove();
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
//    private void updateProjectiles() {
//        // Пули игрока
//        Iterator<Rectangle> iter = playerProjectiles.iterator();
//        while (iter.hasNext()) {
//            Rectangle projectile = iter.next();
//            projectile.y += 400 * Gdx.graphics.getDeltaTime();
//
//            if (projectile.y > Gdx.graphics.getHeight()) {
//                iter.remove();
//            }
//        }
//
//        // Пули босса
//        iter = bossProjectiles.iterator();
//        while (iter.hasNext()) {
//            Rectangle projectile = iter.next();
//            projectile.y -= 300 * Gdx.graphics.getDeltaTime();
//
//            if (projectile.y < 0) {
//                iter.remove();
//            }
//        }
//    }

//    private void updateBossAI() {
//        if (!isBossActive) {
//            // Атака миньонов
//            if (Math.random() < 0.02) {
//                for (Rectangle minion : minions) {
//                    bossProjectiles.add(new Rectangle(
//                            minion.x + minion.width / 2 - 8,
//                            minion.y,
//                            16, 16
//                    ));
//                }
//            }
//            return;
//        }
//
//        // Атака босса
//        /*if (Math.random() < 0.01) {
//            bossProjectiles.add(new Rectangle(
//                    boss.x + boss.width / 2 - 8,
//                    boss.y,
//                    16, 16
//            ));
//        }*/
//    }

