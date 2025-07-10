package me.AI.core.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
        public enum EnemyType {
            SCOUT(50, 5, 100, 10),
            FIGHTER(100, 15, 80, 25),
            CRUISER(200, 30, 60, 50),
            BATTLESHIP(500, 60, 40, 100);

            public final float health;
            public final float damage;
            public final float speed;
            public final int reward;

            EnemyType(float health, float damage, float speed, int reward) {
                this.health = health;
                this.damage = damage;
                this.speed = speed;
                this.reward = reward;
            }
        }

        private Vector2 position;
        private Vector2 velocity;
        private Texture texture;
        private EnemyType type;
        private float health;
        private float maxHealth;
        private boolean destroyed;
        private float attackCooldown;
        private float scale;

        public Enemy(Texture texture, EnemyType type, float x, float y) {
            this.texture = texture;
            this.type = type;
            this.position = new Vector2(x, y);
            this.velocity = new Vector2(-type.speed, MathUtils.random(-50, 50));
            this.health = type.health;
            this.maxHealth = type.health;
            this.destroyed = false;
            this.attackCooldown = 0;
            this.scale = getScaleForType(type);
        }

        private float getScaleForType(EnemyType type) {
            switch(type) {
                case SCOUT: return 0.8f;
                case FIGHTER: return 1.0f;
                case CRUISER: return 1.3f;
                case BATTLESHIP: return 1.6f;
                default: return 1.0f;
            }
        }

        public void update(float delta) {
            if (destroyed) return;

            // Move enemy
            position.add(velocity.x * delta, velocity.y * delta);

            // Update attack cooldown
            attackCooldown -= delta;

            // Destroy if off-screen
            if (position.x < -100) {
                destroyed = true;
            }
        }

        public void render(SpriteBatch batch) {
            if (destroyed) return;

            // Draw enemy with scaling
            batch.draw(texture, position.x, position.y,
                    texture.getWidth() * scale, texture.getHeight() * scale);

            // Draw health bar
            drawHealthBar(batch);
        }

        private void drawHealthBar(SpriteBatch batch) {
            float healthPercent = health / maxHealth;
            float barWidth = texture.getWidth() * scale;
            float barHeight = 5;

            // Background (red)
            batch.setColor(1, 0, 0, 0.8f);
            batch.draw(texture, position.x, position.y + texture.getHeight() * scale + 5,
                    barWidth, barHeight);

            // Foreground (green)
            batch.setColor(0, 1, 0, 0.8f);
            batch.draw(texture, position.x, position.y + texture.getHeight() * scale + 5,
                    barWidth * healthPercent, barHeight);

            batch.setColor(1, 1, 1, 1); // Reset color
        }

        public void takeDamage(float damage) {
            health -= damage;
            if (health <= 0) {
                destroyed = true;
            }
        }

        public boolean canAttack() {
            return attackCooldown <= 0;
        }

        public void attack() {
            attackCooldown = 2.0f; // 2 second cooldown
        }

        // Getters
        public Vector2 getPosition() { return position; }
        public EnemyType getType() { return type; }
        public boolean isDestroyed() { return destroyed; }
        public float getHealth() { return health; }
        public float getMaxHealth() { return maxHealth; }
        public Texture getTexture() { return texture; }
        public float getScale() { return scale; }

}
