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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.Iterator;

public class BossFightScene implements Screen {
    private Game game;
    private SpriteBatch batch;
    private Texture playerTexture, bossTexture, projectileTexture, background;
    private Rectangle player, boss;
    private ArrayList<Bullet> playerProjectiles, bossProjectiles;
    private ArrayList<Rectangle> minions;
    // Константы скоростей
    private static final float PLAYER_PROJECTILE_SPEED = 400f;
    private static final float BOSS_PROJECTILE_SPEED = 300f;
    private int bossHealth;
    private int minionsDefeated = 0;
    private final int MINIONS_TO_DEFEAT = 4;
    private boolean isBossActive = false;
    private int currentMinionIndex = 0;
    private boolean isMinionActive = true;
    private static final int MINION_REWARD = 25;
    private static final int BOSS_REWARD = 100;
    private float minionAttackTimer = 0f;
    private float bossAttackTimer = 0f;
    private int bossAttackPhase = 0;
    private final float MINION_ATTACK_INTERVAL = 1.0f;
    private final float BOSS_ATTACK_INTERVAL = 0.2f;
    private final float BOSS_PHASE_INTERVAL = 5.0f;
    private static final float PLAYER_SPEED = 200f;
    private int playerHealth;
    private int minionHealth;
    private int minionMaxHealth;
    private int bossMaxHealth;
    private int playerDamage;
    private int playerMaxHp;
    private int minionDamage;
    private int bossDamage;
    // Базовые константы для HP и урона
    private static final int BASE_MINION_HP = 20;
    private static final int BASE_MINION_DAMAGE = 1;
    private static final int BASE_BOSS_HP = 100;
    private static final int BASE_BOSS_DAMAGE = 1;
    // Для анимации полосы здоровья
    private float displayedPlayerHealth;
    private float displayedBossHealth;
    private float displayedMinionHealth;
    private float playerHitFlash = 0f;
    private float bossHitFlash = 0f;
    private float defeatMessageTimer = 0f;
    private boolean showDefeatMessage = false;
    private BitmapFont messageFont;
    private Skin skin;
    private Sound playerShootSound;
    private Sound bossShootSound;
    private Sound hitSound;
    private Sound shieldSound;
    private Sound healSound;
    // Переменные для движения
    private float minionMovementTimer = 0f;
    private float bossMovementTimer = 0f;
    // Переменные для щита и исцеления
    private boolean minionShieldActive = false;
    private float minionShieldTimer = 0f;
    private float minionShieldCooldown = 10f;
    private float minionShieldDuration = 3f;
    private boolean bossShieldActive = false;
    private float bossShieldTimer = 0f;
    private float bossShieldCooldown = 15f;
    private float bossShieldDuration = 5f;
    private float minionHealTimer = 0f;
    private float minionHealCooldown = 8f;
    private float bossHealTimer = 0f;
    private float bossHealCooldown = 10f;
    private float minionHealFlash = 0f;
    private float bossHealFlash = 0f;
    // Для движения четвертого миньона
    private Vector2 minionTarget = new Vector2();
    private float minionTargetTimer = 0f;

    public static void addplayerlevel(int value) {
        MainScene.getIntsance(null).playerLevel += value;
        MainScene.getIntsance(null).updateLabelCoin();
    }

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
        bossTexture = new Texture("boss.PNG");
        projectileTexture = new Texture("bullet.png");
        background = new Texture("backgroundboss.png");

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
        // Инициализация HP и урона на основе прокачки
        this.playerDamage = playerDamage;
        this.playerMaxHp = playerMaxHp;
        this.playerHealth = playerMaxHp;
        this.displayedPlayerHealth = playerMaxHp;
        this.minionMaxHealth = BASE_MINION_HP + MainScene.getIntsance(null).upgradeMaxHp * 2;
        this.minionHealth = minionMaxHealth;
        this.minionDamage = BASE_MINION_DAMAGE + MainScene.getIntsance(null).upgradeDamage / 2;
        this.bossMaxHealth = BASE_BOSS_HP + MainScene.getIntsance(null).upgradeMaxHp * 3 + MainScene.getIntsance(null).playerLevel * 5;
        this.bossHealth = bossMaxHealth;
        this.bossDamage = BASE_BOSS_DAMAGE + MainScene.getIntsance(null).upgradeDamage / 2;
        this.displayedBossHealth = bossMaxHealth;
        this.displayedMinionHealth = minionMaxHealth;
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        messageFont = skin.getFont("default-font");

        try {
            playerShootSound = Gdx.audio.newSound(Gdx.files.internal("player_shoot.wav"));
        } catch (Exception e) {
            Gdx.app.log("BossFightScene", "Failed to load player_shoot.wav, sound disabled", e);
            playerShootSound = null;
        }

        try {
            bossShootSound = Gdx.audio.newSound(Gdx.files.internal("boss_shoot.wav"));
        } catch (Exception e) {
            Gdx.app.log("BossFightScene", "Failed to load boss_shoot.wav, sound disabled", e);
            bossShootSound = null;
        }

        try {
            hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
        } catch (Exception e) {
            Gdx.app.log("BossFightScene", "Failed to load hit.wav, sound disabled", e);
            hitSound = null;
        }

        try {
            shieldSound = Gdx.audio.newSound(Gdx.files.internal("shield.wav"));
        } catch (Exception e) {
            Gdx.app.log("BossFightScene", "Failed to load shield.wav, sound disabled", e);
            shieldSound = null;
        }

        try {
            healSound = Gdx.audio.newSound(Gdx.files.internal("heal.wav"));
        } catch (Exception e) {
            Gdx.app.log("BossFightScene", "Failed to load heal.wav, sound disabled", e);
            healSound = null;
        }

        // Инициализация целевой точки для четвертого миньона
        if (isMinionActive && currentMinionIndex == 3) {
            minionTarget.set(
                    50f + (float)Math.random() * (Gdx.graphics.getWidth() - 100f - 80f),
                    Gdx.graphics.getHeight() / 2f + (float)Math.random() * (Gdx.graphics.getHeight() - 100f - 80f - Gdx.graphics.getHeight() / 2f)
            );
        }
    }

    public BossFightScene(Game game, int minionIndex) {
        this(game, minionIndex, MainScene.getIntsance(null).playerLevel, MainScene.getIntsance(null).upgradeDamage, MainScene.getIntsance(null).upgradeMaxHp);
    }


    private void spawnSingleMinion(int index) {
        minions.clear();
        float x = Gdx.graphics.getWidth() / 2f - 40;
        float y = Gdx.graphics.getHeight() - 150;
        minions.add(new Rectangle(x, y, 80, 80));
        // Сбрасываем таймеры и состояние при спавне нового миньона
        minionHealth = minionMaxHealth;
        displayedMinionHealth = minionMaxHealth;
        minionShieldActive = false;
        minionShieldTimer = 0f;
        minionHealTimer = 0f;
        minionMovementTimer = 0f;
        minionHealFlash = 0f;
        if (index == 3) {
            minionTarget.set(
                    50f + (float)Math.random() * (Gdx.graphics.getWidth() - 100f - 80f),
                    Gdx.graphics.getHeight() / 2f + (float)Math.random() * (Gdx.graphics.getHeight() - 100f - 80f - Gdx.graphics.getHeight() / 2f)
            );
        }
    }

    @Override
    public void render(float delta) {
        if (showDefeatMessage) {
            defeatMessageTimer -= delta;
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            String msg = "You have been defeated! -30 coins.\nMinions start over.";
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

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(playerTexture, player.x, player.y, player.width, player.height);

        if (isBossActive) {
            if (bossShieldActive) {
                batch.setColor(0.5f, 0.5f, 1f, 1f);
            } else if (bossHealFlash > 0f) {
                batch.setColor(0.5f, 1f, 0.5f, 1f);
                bossHealFlash -= delta;
            } else {
                batch.setColor(1, 1, 1, 1);
            }
            batch.draw(bossTexture, boss.x, boss.y, boss.width, boss.height);
            batch.setColor(1, 1, 1, 1);
        } else {
            for (Rectangle minion : minions) {
                if (currentMinionIndex == 2 && minionShieldActive) {
                    batch.setColor(0.5f, 0.5f, 1f, 1f);
                } else if (currentMinionIndex == 3 && minionHealFlash > 0f) {
                    batch.setColor(0.5f, 1f, 0.5f, 1f);
                    minionHealFlash -= delta;
                } else {
                    batch.setColor(1, 1, 1, 1);
                }
                batch.draw(bossTexture, minion.x, minion.y, minion.width, minion.height);
                batch.setColor(1, 1, 1, 1);
            }
        }

        for (Bullet projectile : playerProjectiles) {
            batch.draw(projectileTexture, projectile.hitbox.x, projectile.hitbox.y, 16, 16);
        }

        for (Bullet projectile : bossProjectiles) {
            batch.draw(projectileTexture, projectile.hitbox.x, projectile.hitbox.y, 16, 16);
        }

        float interp = 8f * delta;
        displayedPlayerHealth += (playerHealth - displayedPlayerHealth) * interp;
        displayedBossHealth += (bossHealth - displayedBossHealth) * interp;
        displayedMinionHealth += (minionHealth - displayedMinionHealth) * interp;
        if (Math.abs(displayedPlayerHealth - playerHealth) < 0.1f) displayedPlayerHealth = playerHealth;
        if (Math.abs(displayedBossHealth - bossHealth) < 0.1f) displayedBossHealth = bossHealth;
        if (Math.abs(displayedMinionHealth - minionHealth) < 0.1f) displayedMinionHealth = minionHealth;

        if (playerHitFlash > 0f) {
            batch.setColor(1, 0.5f, 0.5f, 1);
            playerHitFlash -= delta;
        } else {
            batch.setColor(1, 1, 1, 1);
        }


        float barWidth = 200f;
        float barHeight = 20f;
        float px = 40f;
        float py = Gdx.graphics.getHeight() - 120f;
        batch.setColor(0.8f, 0.8f, 0.8f, 1);
        batch.draw(background, px, py, barWidth, barHeight);
        batch.setColor(0.5f, 1f, 0.5f, 1);
        batch.draw(background, px, py, barWidth * (displayedPlayerHealth/(float)playerMaxHp), barHeight);
        batch.setColor(1, 1, 1, 1);

        if (bossHitFlash > 0f) {
            batch.setColor(0.7f, 1f, 0.7f, 1);
            bossHitFlash -= delta;
        } else {
            batch.setColor(1, 1, 1, 1);
        }
        float by = Gdx.graphics.getHeight() - 80f;
        float maxHP = isBossActive ? bossMaxHealth : minionMaxHealth;
        float curHP = isBossActive ? displayedBossHealth : displayedMinionHealth;
        batch.setColor(0.8f, 0.8f, 0.8f, 1);
        batch.draw(background, px, by, barWidth, barHeight);
        batch.setColor(1f, 0.5f, 0.5f, 1);
        batch.draw(background, px, by, barWidth * (curHP/maxHP), barHeight);
        batch.setColor(1, 1, 1, 1);

        BitmapFont font = new BitmapFont();
        String bossText = isBossActive ? ("Boss HP: " + bossHealth + "/" + bossMaxHealth) : ("Minion HP: " + minionHealth + "/" + minionMaxHealth);
        font.draw(batch, bossText, px, by + barHeight + 22);
        font.draw(batch, "Player HP: " + playerHealth, px, py + barHeight + 22);
        if (isBossActive) {
            font.draw(batch, "Boss damage: " + bossDamage, px, by + barHeight + 2);
        } else {
            font.draw(batch, "Minion damage: " + minionDamage, px, by + barHeight + 2);
        }
        if (isMinionActive && currentMinionIndex == 2 && minionShieldActive) {
            font.draw(batch, "Minion Shield Active", px, by + barHeight + 42);
        }
        if (isBossActive && bossShieldActive) {
            font.draw(batch, "Boss Shield Active", px, by + barHeight + 42);
        }
        batch.end();
    }

    private void update(float delta) {
        handleInput();
        updateProjectiles();
        if (isMinionActive) {
            updateMinionAttackPattern(delta);
            updateMinionMovement(delta);
            updateMinionMechanics(delta);
        } else if (isBossActive) {
            updateBossAttackPattern(delta);
            updateBossMovement(delta);
            updateBossMechanics(delta);
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
        if (moveX != 0 || moveY != 0) {
            float len = (float)Math.sqrt(moveX*moveX + moveY*moveY);
            moveX /= len;
            moveY /= len;
            player.x += moveX * PLAYER_SPEED * Gdx.graphics.getDeltaTime();
            player.y += moveY * PLAYER_SPEED * Gdx.graphics.getDeltaTime();
            if (player.x < 0) player.x = 0;
            if (player.x + player.width > Gdx.graphics.getWidth()) player.x = Gdx.graphics.getWidth() - player.width;
            if (player.y < 0) player.y = 0;
            if (player.y + player.height > Gdx.graphics.getHeight()) player.y = Gdx.graphics.getHeight() - player.height;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            playerProjectiles.add(new Bullet(
                    player.x + player.width / 2 - 8,
                    player.y + player.height,
                    0,
                    PLAYER_PROJECTILE_SPEED
            ));
            if (playerShootSound != null) {
                playerShootSound.play(0.5f);
            }
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
                        if (!(currentMinionIndex == 2 && minionShieldActive)) {
                            minionHealth -= 5 * MainScene.getIntsance(null).playerLevel;
                            bossHitFlash = 0.5f;
                            if (hitSound != null) {
                                hitSound.play(0.3f);
                            }
                        }
                        if (minionHealth <= 0) {
                            minionIter.remove();
                            MainScene.addCoin(MINION_REWARD);
                            MainScene.getIntsance(null).updateLabelCoin();
                            MainScene.getIntsance(null).minionStage++;
                            game.setScreen(MainScene.getIntsance(game));
                            return;
                        }
                        break;
                    }
                }
            } else if (isBossActive) {
                if (projectile.hitbox.overlaps(boss)) {
                    if (!bossShieldActive) {
                        bossHealth -= 5 * MainScene.getIntsance(null).playerLevel;
                        bossHitFlash = 0.5f;
                        if (hitSound != null) {
                            hitSound.play(0.3f);
                        }
                    }
                    projectileIter.remove();
                    if (bossHealth <= 0) {
                        MainScene.addCoin(BOSS_REWARD);
                        MainScene.getIntsance(null).updateLabelCoin();
                        MainScene.getIntsance(null).minionStage = 0;
                        game.setScreen(MainScene.getIntsance(game));
                        return;
                    }
                }
            }
        }
        for (Bullet projectile : bossProjectiles) {
            if (projectile.hitbox.overlaps(player)) {
                playerHealth -= isBossActive ? bossDamage : minionDamage;
                playerHitFlash = 0.5f;
                bossProjectiles.remove(projectile);
                if (hitSound != null) {
                    hitSound.play(0.3f);
                }
                Gdx.app.log("BossFight", "Player hit!");
                if (playerHealth <= 0) {
                    int lost = Math.min(30, MainScene.getIntsance(null).coins);
                    MainScene.getIntsance(null).coins -= lost;
                    MainScene.getIntsance(null).updateLabelCoin();
                    MainScene.getIntsance(null).minionStage = 0;
                    showDefeatMessage = true;
                    defeatMessageTimer = 2f;
                    return;
                }
                break;
            }
        }
    }

    private void updateBossMovement(float delta) {
        if (!isBossActive) return;
        bossMovementTimer += delta;
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() - 150;
        float minX = 50f;
        float maxX = Gdx.graphics.getWidth() - 50f - boss.width;
        float minY = Gdx.graphics.getHeight() / 2f;
        float maxY = Gdx.graphics.getHeight() - 100f - boss.height;
        float speed = 150f;


        switch (bossAttackPhase) {
            case 0:
                float radius = 100f + 50f * (float)Math.sin(bossMovementTimer * 0.5f);
                float angle = bossMovementTimer * 2f;
                boss.x = centerX + (float)Math.cos(angle) * radius - boss.width / 2;
                boss.y = centerY + (float)Math.sin(angle) * radius - boss.height / 2;
                break;
            case 1:
                boss.x = centerX + (float)Math.sin(bossMovementTimer * 1.5f) * 250f - boss.width / 2;
                boss.y = centerY + (float)Math.cos(bossMovementTimer * 3f) * 50f - boss.height / 2;
                if (bossMovementTimer % 1f < 0.1f) {
                    boss.x += (float)Math.random() * 100f - 50f;
                }
                break;
            case 2:
                if (bossMovementTimer >= 1f) {
                    boss.x = minX + (float)Math.random() * (maxX - minX) - boss.width / 2;
                    boss.y = minY + (float)Math.random() * (maxY - minY) - boss.height / 2;
                    bossMovementTimer = 0f;
                }
                break;
            case 3:
                float dx = (player.x + player.width / 2) - (boss.x + boss.width / 2);
                float dy = (player.y + player.height / 2) - (boss.y + boss.height / 2);
                float len = (float)Math.sqrt(dx * dx + dy * dy);
                if (len > 0) {
                    boss.x += (dx / len) * speed * delta + ((float)Math.random() * 20f - 10f);
                    boss.y += (dy / len) * speed * delta + ((float)Math.random() * 20f - 10f);
                }
                break;
            case 4:
                float ellipseX = 200f * (float)Math.cos(bossMovementTimer * 1f);
                float ellipseY = 100f * (float)Math.sin(bossMovementTimer * 1.5f);
                boss.x = centerX + ellipseX - boss.width / 2;
                boss.y = centerY + ellipseY - boss.height / 2;
                break;
        }

        if (boss.x < minX) boss.x = minX;
        if (boss.x > maxX) boss.x = maxX;
        if (boss.y < minY) boss.y = minY;
        if (boss.y > maxY) boss.y = maxY;
    }

    private void updateMinionMovement(float delta) {
        if (!isMinionActive || minions.isEmpty()) return;
        minionMovementTimer += delta;
        Rectangle minion = minions.get(0);
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() - 150;
        float speed = 100f;
        float minX = 50f;
        float maxX = Gdx.graphics.getWidth() - 50f - minion.width;
        float minY = Gdx.graphics.getHeight() / 2f;
        float maxY = Gdx.graphics.getHeight() - 100f - minion.height;


        switch (currentMinionIndex) {
            case 0:
                float radius = 100f;
                float angle = minionMovementTimer * 2f;
                minion.x = centerX + (float)Math.cos(angle) * radius - minion.width / 2;
                minion.y = centerY + (float)Math.sin(angle) * radius - minion.height / 2;
                break;
            case 1:
                minion.x = centerX + (float)Math.sin(minionMovementTimer * 1.5f) * 200f - minion.width / 2;
                minion.y = centerY - minion.height / 2;
                if (minion.x < minX) minion.x = minX;
                if (minion.x > maxX) minion.x = maxX;
                break;
            case 2:
                float zigzagSpeed = speed * delta;
                minion.x += zigzagSpeed * (Math.sin(minionMovementTimer * 2f) > 0 ? 1 : -1);
                minion.y = centerY + (float)Math.cos(minionMovementTimer * 1f) * 100f - minion.height / 2;
                if (minion.x < minX) minion.x = minX;
                if (minion.x > maxX) minion.x = maxX;
                if (minion.y < minY) minion.y = minY;
                if (minion.y > maxY) minion.y = maxY;
                break;
            case 3:
                minionTargetTimer += delta;
                if (minionTargetTimer >= 1f + (float)Math.random() * 1f) {
                    minionTarget.set(
                            minX + (float)Math.random() * (maxX - minX),
                            minY + (float)Math.random() * (maxY - minY)
                    );
                    minionTargetTimer = 0f;
                }
                float dx = minionTarget.x - (minion.x + minion.width / 2);
                float dy = minionTarget.y - (minion.y + minion.height / 2);
                float len = (float)Math.sqrt(dx * dx + dy * dy);
                if (len > 5f) {
                    minion.x += (dx / len) * speed * delta;
                    minion.y += (dy / len) * speed * delta;
                }
                if (minion.x < minX) minion.x = minX;
                if (minion.x > maxX) minion.x = maxX;
                if (minion.y < minY) minion.y = minY;
                if (minion.y > maxY) minion.y = maxY;
                break;
        }
    }

    private void updateMinionMechanics(float delta) {
        if (!isMinionActive || minions.isEmpty()) return;
        if (currentMinionIndex == 2) {
            minionShieldTimer += delta;
            if (minionShieldActive) {
                if (minionShieldTimer >= minionShieldDuration) {
                    minionShieldActive = false;
                    minionShieldTimer = 0f;
                }
            } else {
                if (minionShieldTimer >= minionShieldCooldown) {
                    minionShieldActive = true;
                    minionShieldTimer = 0f;
                    if (shieldSound != null) {
                        shieldSound.play(0.5f);
                    }
                }
            }
        }
        if (currentMinionIndex == 3) {
            minionHealTimer += delta;
            if (minionHealTimer >= minionHealCooldown) {
                minionHealth = Math.min(minionHealth + 5, minionMaxHealth);
                minionHealTimer = 0f;
                minionHealFlash = 0.5f;
                if (healSound != null) {
                    healSound.play(0.5f);
                }
            }
        }
    }


    private void updateBossMechanics(float delta) {
        if (!isBossActive) return;
        bossShieldTimer += delta;
        if (bossShieldActive) {
            if (bossShieldTimer >= bossShieldDuration) {
                bossShieldActive = false;
                bossShieldTimer = 0f;
            }
        } else {
            if (bossShieldTimer >= bossShieldCooldown) {
                bossShieldActive = true;
                bossShieldTimer = 0f;
                if (shieldSound != null) {
                    shieldSound.play(0.5f);
                }
            }
        }
        if (!bossShieldActive) {
            bossHealTimer += delta;
            if (bossHealTimer >= bossHealCooldown) {
                bossHealth = Math.min(bossHealth + 10, bossMaxHealth);
                bossHealTimer = 0f;
                bossHealFlash = 0.5f;
                if (healSound != null) {
                    healSound.play(0.5f);
                }
            }
        }
    }

    private void updateBossAttackPattern(float delta) {
        bossAttackTimer += delta;
        if (bossAttackTimer >= BOSS_ATTACK_INTERVAL) {
            bossAttackTimer = 0f;
            switch (bossAttackPhase) {
                case 0:
                    float angle = (Gdx.graphics.getFrameId() * 8) % 360;
                    createSpiralProjectile(angle);
                    break;
                case 1:
                    createFanAttack(5, 60f);
                    break;
                case 2:
                    createCircleAttack(12);
                    break;
                case 3:
                    createTargetedAttack(3);
                    break;
                case 4:
                    createWaveAttack((int)(Gdx.graphics.getFrameId() / 30) % 5);
                    break;
            }
        }
        bossAttackPhase = (int)((Gdx.graphics.getFrameId() * Gdx.graphics.getDeltaTime()) / BOSS_PHASE_INTERVAL) % 5;
    }

    private void updateMinionAttackPattern(float delta) {
        minionAttackTimer += delta;
        if (minionAttackTimer >= MINION_ATTACK_INTERVAL && !minions.isEmpty()) {
            minionAttackTimer = 0f;
            Rectangle minion = minions.get(0);
            switch (currentMinionIndex) {
                case 0:
                    bossProjectiles.add(createProjectile(
                            minion.x + minion.width/2 - 8,
                            minion.y,
                            0,
                            -BOSS_PROJECTILE_SPEED
                    ));
                    break;
                case 1:
                    for (int i = -1; i <= 1; i++) {
                        float angle = (float)Math.toRadians(270 + i*20);
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
                case 2:
                    for (int i = 0; i < 3; i++) {
                        bossProjectiles.add(createProjectile(
                                minion.x + minion.width/2 - 40 + i*30,
                                minion.y,
                                0,
                                -BOSS_PROJECTILE_SPEED
                        ));
                    }
                    break;
                case 3:
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
            if (bossShootSound != null) {
                bossShootSound.play(0.2f);
            }
        }
    }

    private void createSpiralProjectile(float angle) {
        float rad = (float)Math.toRadians(angle);
        float speed = 200f;
        bossProjectiles.add(createProjectile(
                boss.x + boss.width/2 - 8,
                boss.y,
                (float)Math.sin(rad) * speed,
                (float)Math.cos(rad) * -speed
        ));
        if (bossShootSound != null) {
            bossShootSound.play(0.3f);
        }
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
        if (bossShootSound != null) {
            bossShootSound.play(0.3f);
        }
    }

    private void createTargetedAttack(int count) {
        for (int i = 0; i < count; i++) {
            float offsetX = (float)Math.random() * 40 - 20;
            float offsetY = (float)Math.random() * 40 - 20;
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
        if (bossShootSound != null) {
            bossShootSound.play(0.3f);
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
        if (bossShootSound != null) {
            bossShootSound.play(0.3f);
        }
    }

    private void createWaveAttack(int waveType) {
        float speed = 220f;
        int waves = 3 + waveType;

        for (int i = 0; i < waves; i++) {
            float offset = i * 30f;
            bossProjectiles.add(createProjectile(
                    boss.x + boss.width/2 - 8 + offset,
                    boss.y,
                    0,
                    -speed
            ));
            bossProjectiles.add(createProjectile(
                    boss.x + boss.width/2 - 8,
                    boss.y - offset,
                    speed,
                    0
            ));
        }
        if (bossShootSound != null) {
            bossShootSound.play(0.3f);
        }
    }

    private Bullet createProjectile(float x, float y, float velX, float velY) {
        return new Bullet(x, y, velX, velY);
    }


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
        if (playerShootSound != null) {
            playerShootSound.dispose();
        }
        if (bossShootSound != null) {
            bossShootSound.dispose();
        }
        if (hitSound != null) {
            hitSound.dispose();
        }
        if (shieldSound != null) {
            shieldSound.dispose();
        }
        if (healSound != null) {
            healSound.dispose();
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
