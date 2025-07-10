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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class BossFightScene implements Screen {
    private Game game;
    private SpriteBatch batch;
    private Texture playerTexture, bossTexture, projectileTexture, background;
    private Rectangle player, boss;
    private ArrayList<Bullet> playerProjectiles, bossProjectiles;
    private ArrayList<Rectangle> minions;
    // Константы скоростей
    private static final float PLAYER_PROJECTILE_SPEED = 400f;
    private static final float BOSS_PROJECTILE_SPEED = 300f; // Общая скорость снарядов босса
    private static int playerLevel = 1;
    private int bossHealth = 100;
    private int minionsDefeated = 0;
    private final int MINIONS_TO_DEFEAT = 4;
    private boolean isBossActive = false;
    private int currentMinionIndex = 0; // Индекс текущего приспешника
    private boolean isMinionActive = true; // Флаг: сейчас бой с приспешником
    private static final int MINION_REWARD = 25;
    private static final int BOSS_REWARD = 100;
    private float minionAttackTimer = 0f;
    private float bossAttackTimer = 0f;
    private int bossAttackPhase = 0;
    private final float MINION_ATTACK_INTERVAL = 1.0f;
    private final float BOSS_ATTACK_INTERVAL = 0.2f;
    private final float BOSS_PHASE_INTERVAL = 5.0f;
    private static final float PLAYER_SPEED = 200f;
    private int playerHealth = 5;
    private int minionHealth = 20;
    private final int minionMaxHealth = 20;
    private final int bossMaxHealth = 100;
    private int playerDamage = 1;
    private int playerMaxHp = 5;
    
    // Для анимации полосы здоровья
    private float displayedPlayerHealth = 5;
    private float displayedBossHealth = bossMaxHealth;
    private float displayedMinionHealth = minionMaxHealth;
    private float playerHitFlash = 0f;
    private float bossHitFlash = 0f;
    private float defeatMessageTimer = 0f;
    private boolean showDefeatMessage = false;
    private BitmapFont messageFont;
    private Skin skin;

    public static void addplayerlevel(int value){
        playerLevel+=value;
        MainScene.getIntsance1().updateLabelCoin();
    }

    // Внутренний класс Bullet
    private static class Bullet {
        Rectangle hitbox;
        Vector2 velocity;
        Bullet(float x, float y, float vx, float vy) {
            this.hitbox = new Rectangle(x, y, 16, 16);
            this.velocity = new Vector2(vx, vy);
        }
    }

    public BossFightScene(Game game, int minionIndex, int playerLevel, int playerDamage, int playerMaxHp) {
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
        this.game = game;
        this.currentMinionIndex = minionIndex;
        this.isMinionActive = (minionIndex < MINIONS_TO_DEFEAT);
        this.isBossActive = (minionIndex >= MINIONS_TO_DEFEAT);
        if (isMinionActive) {
            spawnSingleMinion(currentMinionIndex);
        }
        this.bossHealth = bossMaxHealth;
        this.minionHealth = minionMaxHealth;
        // --- Передаём апгрейды ---
        this.playerLevel = playerLevel;
        this.playerDamage = playerDamage;
        this.playerMaxHp = playerMaxHp;
        this.playerHealth = playerMaxHp;
        this.displayedPlayerHealth = playerMaxHp;
        // Используем шрифт из uiskin, поддерживающий кириллицу
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        messageFont = skin.getFont("default-font");
    }
    // Старый конструктор для обратной совместимости
    public BossFightScene(Game game, int minionIndex) {
        this(game, minionIndex, MainScene.getIntsance1().playerLevel, MainScene.getIntsance1().upgradeDamage, MainScene.getIntsance1().upgradeMaxHp);
    }
    // Спавн только одного приспешника по индексу
    private void spawnSingleMinion(int index) {
        minions.clear();
        minions.add(new Rectangle(
                Gdx.graphics.getWidth() / 2f - 40,
                Gdx.graphics.getHeight() - 150,
                80, 80
        ));
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
        if (showDefeatMessage) {
            defeatMessageTimer -= delta;
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            // Используем messageFont для кириллицы
            String msg = "Вы потерпели поражение! -30 золота.\nПриспешники начинаются заново.";
            messageFont.getData().setScale(1.5f);
            messageFont.setColor(1, 0.2f, 0.2f, 1);
            messageFont.draw(batch, msg, Gdx.graphics.getWidth()/2f - 250, Gdx.graphics.getHeight()/2f + 30);
            batch.end();
            if (defeatMessageTimer <= 0f) {
                showDefeatMessage = false;
                game.setScreen(MainScene.getIntsance(game));
            }
            return;
        }
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
        for (Bullet projectile : playerProjectiles) {
            batch.draw(projectileTexture, projectile.hitbox.x, projectile.hitbox.y, 16, 16);
        }

        // Пули босса
        for (Bullet projectile : bossProjectiles) {
            batch.draw(projectileTexture, projectile.hitbox.x, projectile.hitbox.y, 16, 16);
        }

        // --- Анимация полосы здоровья ---
        float interp = 8f * delta;
        displayedPlayerHealth += (playerHealth - displayedPlayerHealth) * interp;
        displayedBossHealth += (bossHealth - displayedBossHealth) * interp;
        displayedMinionHealth += (minionHealth - displayedMinionHealth) * interp;
        if (Math.abs(displayedPlayerHealth - playerHealth) < 0.1f) displayedPlayerHealth = playerHealth;
        if (Math.abs(displayedBossHealth - bossHealth) < 0.1f) displayedBossHealth = bossHealth;
        if (Math.abs(displayedMinionHealth - minionHealth) < 0.1f) displayedMinionHealth = minionHealth;
        // --- Вспышки при уроне ---
        if (playerHitFlash > 0f) {
            batch.setColor(1, 0.5f, 0.5f, 1);
            playerHitFlash -= delta;
        } else {
            batch.setColor(1, 1, 1, 1);
        }
        // --- Полоса здоровья игрока ---
        float barWidth = 200f;
        float barHeight = 20f;
        float px = 40f; // Слева
        float py = Gdx.graphics.getHeight() - 120f; // Чуть ниже верха
        batch.setColor(0.8f, 0.8f, 0.8f, 1); // светлый фон полосы
        batch.draw(background, px, py, barWidth, barHeight);
        batch.setColor(0.5f, 1f, 0.5f, 1); // ярко-зелёная полоса
        batch.draw(background, px, py, barWidth * (displayedPlayerHealth/(float)playerMaxHp), barHeight);
        batch.setColor(1, 1, 1, 1);
        // --- Полоса здоровья босса/приспешника ---
        if (bossHitFlash > 0f) {
            batch.setColor(0.7f, 1f, 0.7f, 1);
            bossHitFlash -= delta;
        } else {
            batch.setColor(1, 1, 1, 1);
        }
        float by = Gdx.graphics.getHeight() - 80f;
        float maxHP = isBossActive ? bossMaxHealth : minionMaxHealth;
        float curHP = isBossActive ? displayedBossHealth : displayedMinionHealth;
        batch.setColor(0.8f, 0.8f, 0.8f, 1); // светлый фон полосы
        batch.draw(background, px, by, barWidth, barHeight);
        batch.setColor(1f, 0.5f, 0.5f, 1); // ярко-красная полоса
        batch.draw(background, px, by, barWidth * (curHP/maxHP), barHeight);
        batch.setColor(1, 1, 1, 1);
        // --- Текстовое отображение ---
        BitmapFont font = new BitmapFont();
        String bossText = isBossActive ? ("Boss HP: " + bossHealth + "/" + bossMaxHealth) : ("Minion HP: " + minionHealth + "/" + minionMaxHealth);
        font.draw(batch, bossText, px, by + barHeight + 22);
        font.draw(batch, "Player HP: " + playerHealth, px, py + barHeight + 22);
        if (isBossActive) {
            font.draw(batch, "Boss damage: 1", px, by + barHeight + 2);
        }
        batch.end();
    }

    private void update(float delta) {
        handleInput();
        updateProjectiles();
        if (isMinionActive) {
            updateMinionAttackPattern(delta);
        } else if (isBossActive) {
            updateBossAttackPattern(delta);
        }
        checkCollisions();
    }

    private void handleInput() {
        float moveX = 0f;
        float moveY = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveX -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveX += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveY += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveY -= 1f;
        }
        // Нормализация для диагоналей
        if (moveX != 0 || moveY != 0) {
            float len = (float)Math.sqrt(moveX*moveX + moveY*moveY);
            moveX /= len;
            moveY /= len;
            player.x += moveX * PLAYER_SPEED * Gdx.graphics.getDeltaTime();
            player.y += moveY * PLAYER_SPEED * Gdx.graphics.getDeltaTime();
            // Ограничение рамками экрана
            if (player.x < 0) player.x = 0;
            if (player.x + player.width > Gdx.graphics.getWidth()) player.x = Gdx.graphics.getWidth() - player.width;
            if (player.y < 0) player.y = 0;
            if (player.y + player.height > Gdx.graphics.getHeight()) player.y = Gdx.graphics.getHeight() - player.height;
        }
        // Стрельба (Пробел)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            playerProjectiles.add(new Bullet(
                player.x + player.width / 2 - 8,
                player.y + player.height,
                0,
                PLAYER_PROJECTILE_SPEED
            ));
        }
    }


    private void checkCollisions() {
        Iterator<Bullet> projectileIter = playerProjectiles.iterator();
        while (projectileIter.hasNext()) {
            Bullet projectile = projectileIter.next();
            if (isMinionActive) {
                Iterator<Rectangle> minionIter = minions.iterator();
                while (minionIter.hasNext()) {
                    Rectangle minion = minionIter.next();
                    if (projectile.hitbox.overlaps(minion)) {
                        projectileIter.remove();
                        minionHealth -= 5 * playerLevel;
                        bossHitFlash = 0.5f;
                        if (minionHealth <= 0) {
                            minionIter.remove();
                            MainScene.addCoin(MINION_REWARD);
                            MainScene.getIntsance1().updateLabelCoin();
                            MainScene.getIntsance1().minionStage++;
                            game.setScreen(MainScene.getIntsance(game));
                            return;
                        }
                        break;
                    }
                }
            } else if (isBossActive) {
                if (projectile.hitbox.overlaps(boss)) {
                    bossHealth -= 5 * playerLevel;
                    bossHitFlash = 0.5f;
                    projectileIter.remove();
                    if (bossHealth <= 0) {
                        MainScene.addCoin(BOSS_REWARD);
                        MainScene.getIntsance1().updateLabelCoin();
                        MainScene.getIntsance1().minionStage = 0;
                        game.setScreen(MainScene.getIntsance(game));
                        return;
                    }
                }
            }
        }
        for (Bullet projectile : bossProjectiles) {
            if (projectile.hitbox.overlaps(player)) {
                playerHealth--;
                playerHitFlash = 0.5f;
                bossProjectiles.remove(projectile);
                Gdx.app.log("BossFight", "Player hit!");
                if (playerHealth <= 0) {
                    // Проигрыш: сообщение, минус 30 золота, сброс minionStage
                    int lost = Math.min(30, MainScene.getIntsance1().coins);
                    MainScene.getIntsance1().coins -= lost;
                    MainScene.getIntsance1().updateLabelCoin();
                    MainScene.getIntsance1().minionStage = 0;
                    showDefeatMessage = true;
                    defeatMessageTimer = 2f;
                    return;
                }
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
                bossProjectiles.add(createProjectile(
                    minion.x + minion.width / 2 - 8,
                    minion.y,
                    0,
                    -BOSS_PROJECTILE_SPEED
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
            float rad = (float)Math.toRadians(angle);
            float vx = (float)Math.sin(rad) * BOSS_PROJECTILE_SPEED;
            float vy = (float)Math.cos(rad) * -BOSS_PROJECTILE_SPEED;
            bossProjectiles.add(createProjectile(
                    boss.x + boss.width/2 - 8,
                    boss.y,
                    vx,
                    vy
            ));
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
            float vx = (float)Math.cos(rad) * speed;
            float vy = (float)Math.sin(rad) * speed;
            bossProjectiles.add(createProjectile(
                    boss.x + boss.width/2 - 8,
                    boss.y,
                    vx,
                    vy
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

    private Bullet createProjectile(float x, float y, float velX, float velY) {
        return new Bullet(x, y, velX, velY);
    }

    // --- Новый метод: атаки приспешников по паттерну ---
    private void updateMinionAttackPattern(float delta) {
        minionAttackTimer += delta;
        if (minionAttackTimer >= MINION_ATTACK_INTERVAL && !minions.isEmpty()) {
            minionAttackTimer = 0f;
            Rectangle minion = minions.get(0);
            switch (currentMinionIndex) {
                case 0: // Прямой выстрел вниз
                    bossProjectiles.add(createProjectile(
                        minion.x + minion.width/2 - 8,
                        minion.y,
                        0,
                        -BOSS_PROJECTILE_SPEED
                    ));
                    break;
                case 1: // Веер
                    for (int i = -1; i <= 1; i++) {
                        float angle = (float)Math.toRadians(270 + i*20); // 270 — вниз
                        float vx = (float)Math.cos(angle) * BOSS_PROJECTILE_SPEED;
                        float vy = (float)Math.sin(angle) * BOSS_PROJECTILE_SPEED;
                        bossProjectiles.add(createProjectile(
                            minion.x + minion.width/2 - 8,
                            minion.y,
                            vx,
                            vy
                        ));
                    }
                    break;
                case 2: // Волна (несколько подряд)
                    for (int i = 0; i < 3; i++) {
                        bossProjectiles.add(createProjectile(
                            minion.x + minion.width/2 - 40 + i*30,
                            minion.y,
                            0,
                            -BOSS_PROJECTILE_SPEED
                        ));
                    }
                    break;
                case 3: // Прицельно в игрока
                    float dx = (player.x + player.width/2) - (minion.x + minion.width/2);
                    float dy = (player.y + player.height/2) - minion.y;
                    float len = (float)Math.sqrt(dx*dx + dy*dy);
                    if (len > 0) {
                        float vx = dx/len * BOSS_PROJECTILE_SPEED;
                        float vy = dy/len * BOSS_PROJECTILE_SPEED;
                        bossProjectiles.add(createProjectile(
                            minion.x + minion.width/2 - 8,
                            minion.y,
                            vx,
                            vy
                        ));
                    }
                    break;
            }
        }
    }

    // --- Новый метод: фазовые атаки босса ---
    private void updateBossAttackPattern(float delta) {
        bossAttackTimer += delta;
        if (bossAttackTimer >= BOSS_ATTACK_INTERVAL) {
            bossAttackTimer = 0f;
            switch (bossAttackPhase) {
                case 0: // Спираль
                    float angle = (Gdx.graphics.getFrameId() * 8) % 360;
                    createSpiralProjectile(angle);
                    break;
                case 1: // Веер
                    createFanAttack(5, 60f);
                    break;
                case 2: // Круг
                    createCircleAttack(12);
                    break;
                case 3: // Прицельная
                    createTargetedAttack(3);
                    break;
                case 4: // Волна
                    createWaveAttack((int)(Gdx.graphics.getFrameId() / 30) % 5);
                    break;
            }
        }
        // Смена фазы атаки
        bossAttackPhase = (int)((Gdx.graphics.getFrameId() * Gdx.graphics.getDeltaTime()) / BOSS_PHASE_INTERVAL) % 5;
    }

    // --- Обновлённый updateProjectiles: теперь учитывает velX/velY ---
    private void updateProjectiles() {
        Iterator<Bullet> iter = playerProjectiles.iterator();
        while (iter.hasNext()) {
            Bullet projectile = iter.next();
            projectile.hitbox.x += projectile.velocity.x * Gdx.graphics.getDeltaTime();
            projectile.hitbox.y += projectile.velocity.y * Gdx.graphics.getDeltaTime();
            if (projectile.hitbox.y > Gdx.graphics.getHeight() || projectile.hitbox.x < 0 || projectile.hitbox.x > Gdx.graphics.getWidth()) iter.remove();
        }
        iter = bossProjectiles.iterator();
        while (iter.hasNext()) {
            Bullet projectile = iter.next();
            projectile.hitbox.x += projectile.velocity.x * Gdx.graphics.getDeltaTime();
            projectile.hitbox.y += projectile.velocity.y * Gdx.graphics.getDeltaTime();
            if (projectile.hitbox.y < 0 || projectile.hitbox.x < 0 || projectile.hitbox.x > Gdx.graphics.getWidth() || projectile.hitbox.y > Gdx.graphics.getHeight()) {
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

