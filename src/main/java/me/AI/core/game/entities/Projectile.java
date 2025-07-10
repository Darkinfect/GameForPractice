package me.AI.core.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private Vector2 position;
    private Vector2 velocity;
    private Texture texture;
    private float damage;
    private boolean destroyed;
    private float lifetime;
    private float maxLifetime;

    public Projectile(Texture texture, float x, float y, float damage) {
        this.texture = texture;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(400, 0); // Move right
        this.damage = damage;
        this.destroyed = false;
        this.lifetime = 0;
        this.maxLifetime = 3.0f; // 3 seconds max
    }

    public void update(float delta) {
        if (destroyed) return;

        position.add(velocity.x * delta, velocity.y * delta);
        lifetime += delta;

        // Destroy if too old or off-screen
        if (lifetime >= maxLifetime || position.x > 900) {
            destroyed = true;
        }
    }

    public void render(SpriteBatch batch) {
        if (!destroyed) {
            batch.draw(texture, position.x, position.y);
        }
    }

    public boolean checkCollision(Enemy enemy) {
        if (destroyed || enemy.isDestroyed()) return false;

        Vector2 enemyPos = enemy.getPosition();
        float enemySize = enemy.getTexture().getWidth() * enemy.getScale();

        return position.x < enemyPos.x + enemySize &&
                position.x + texture.getWidth() > enemyPos.x &&
                position.y < enemyPos.y + enemySize &&
                position.y + texture.getHeight() > enemyPos.y;
    }

    public void destroy() {
        destroyed = true;
    }

    // Getters
    public Vector2 getPosition() { return position; }
    public float getDamage() { return damage; }
    public boolean isDestroyed() { return destroyed; }
}
