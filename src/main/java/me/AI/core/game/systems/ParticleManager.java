package me.AI.core.game.systems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;


public class ParticleManager {
    private List<Particle> particles;
    private Texture starTexture;
    private Texture explosionTexture;

    public ParticleManager(Texture starTexture, Texture explosionTexture) {
        this.particles = new ArrayList<>();
        this.starTexture = starTexture;
        this.explosionTexture = explosionTexture;

        // Create background stars
        createBackgroundStars();
    }

    private void createBackgroundStars() {
        for (int i = 0; i < 100; i++) {
            float x = MathUtils.random(0, 800);
            float y = MathUtils.random(0, 600);
            float speed = MathUtils.random(10, 50);
            particles.add(new Particle(starTexture, x, y, -speed, 0, 10.0f, 0.3f));
        }
    }

    public void update(float delta) {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle particle = particles.get(i);
            particle.update(delta);

            if (particle.isDestroyed()) {
                particles.remove(i);
            }
        }

        // Add new background stars
        if (MathUtils.random() < 0.1f) {
            float y = MathUtils.random(0, 600);
            float speed = MathUtils.random(10, 50);
            particles.add(new Particle(starTexture, 800, y, -speed, 0, 10.0f, 0.3f));
        }
    }

    public void render(SpriteBatch batch) {
        for (Particle particle : particles) {
            particle.render(batch);
        }
    }

    public void createExplosion(float x, float y) {
        for (int i = 0; i < 20; i++) {
            float velX = MathUtils.random(-200, 200);
            float velY = MathUtils.random(-200, 200);
            particles.add(new Particle(explosionTexture, x, y, velX, velY, 1.0f, 0.8f));
        }
    }

    private static class Particle {
        private Vector2 position;
        private Vector2 velocity;
        private Texture texture;
        private float lifetime;
        private float maxLifetime;
        private float alpha;
        private boolean destroyed;

        public Particle(Texture texture, float x, float y, float velX, float velY, float lifetime, float alpha) {
            this.texture = texture;
            this.position = new Vector2(x, y);
            this.velocity = new Vector2(velX, velY);
            this.lifetime = 0;
            this.maxLifetime = lifetime;
            this.alpha = alpha;
            this.destroyed = false;
        }

        public void update(float delta) {
            position.add(velocity.x * delta, velocity.y * delta);
            lifetime += delta;

            // Fade out over time
            alpha = 1.0f - (lifetime / maxLifetime);

            if (lifetime >= maxLifetime || position.x < -50) {
                destroyed = true;
            }
        }

        public void render(SpriteBatch batch) {
            batch.setColor(1, 1, 1, alpha);
            batch.draw(texture, position.x, position.y);
            batch.setColor(1, 1, 1, 1);
        }

        public boolean isDestroyed() {
            return destroyed;
        }
    }
}
