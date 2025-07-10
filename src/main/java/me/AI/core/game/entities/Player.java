package me.AI.core.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 position;
    private Texture texture;
    private float health;
    private float maxHealth;
    private float damage;
    private float speed;
    private float fireRate;
    private float lastFireTime;

    // Upgrade levels
    private int damageLevel;
    private int healthLevel;
    private int speedLevel;
    private int fireRateLevel;

    public Player(Texture texture, float x, float y) {
        this.texture = texture;
        this.position = new Vector2(x, y);

        // Base stats
        this.maxHealth = 100;
        this.health = maxHealth;
        this.damage = 10;
        this.speed = 200;
        this.fireRate = 0.5f; // shots per second

        // Upgrade levels
        this.damageLevel = 1;
        this.healthLevel = 1;
        this.speedLevel = 1;
        this.fireRateLevel = 1;
    }

    public void update(float delta) {
        lastFireTime += delta;

        // Auto-regenerate health slowly
        if (health < maxHealth) {
            health += 5 * delta;
            if (health > maxHealth) health = maxHealth;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public boolean canFire() {
        return lastFireTime >= (1f / fireRate);
    }

    public void fire() {
        if (canFire()) {
            lastFireTime = 0;
        }
    }

    public void takeDamage(float damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public void upgradeHealth() {
        healthLevel++;
        float healthIncrease = 25 * healthLevel;
        maxHealth += healthIncrease;
        health += healthIncrease; // Also heal when upgrading
    }

    public void upgradeDamage() {
        damageLevel++;
        damage += 5 * damageLevel;
    }

    public void upgradeSpeed() {
        speedLevel++;
        speed += 50 * speedLevel;
    }

    public void upgradeFireRate() {
        fireRateLevel++;
        fireRate += 0.2f * fireRateLevel;
    }

    // Getters
    public Vector2 getPosition() { return position; }
    public float getHealth() { return health; }
    public float getMaxHealth() { return maxHealth; }
    public float getDamage() { return damage; }
    public float getSpeed() { return speed; }
    public int getDamageLevel() { return damageLevel; }
    public int getHealthLevel() { return healthLevel; }
    public int getSpeedLevel() { return speedLevel; }
    public int getFireRateLevel() { return fireRateLevel; }
}
