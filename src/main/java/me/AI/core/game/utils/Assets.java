package me.AI.core.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Assets {

        public AssetManager manager;

        // Textures
        public Texture playerShip;
        public Texture enemyShip;
        public Texture background;
        public Texture laser;
        public Texture explosion;
        public Texture star;

        // Sounds
        public Sound laserSound;
        public Sound explosionSound;
        public Sound upgradeSound;
        public Sound backgroundMusic;

        // Fonts
        public BitmapFont font;
        public BitmapFont titleFont;

        public Assets() {
            manager = new AssetManager();
        }

        public void load() {
            // Load textures (you'll need to create these or use placeholder rectangles)
            manager.load("player_ship.png", Texture.class);
            manager.load("enemy_ship.png", Texture.class);
            manager.load("space_background.png", Texture.class);
            manager.load("laser.png", Texture.class);
            manager.load("explosion.png", Texture.class);
            manager.load("star.png", Texture.class);

            // Load sounds
            manager.load("laser.ogg", Sound.class);
            manager.load("explosion.ogg", Sound.class);
            manager.load("upgrade.ogg", Sound.class);

            // Create fonts
            createFonts();
        }

        private void createFonts() {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/space_font.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

            parameter.size = 24;
            font = generator.generateFont(parameter);

            parameter.size = 48;
            titleFont = generator.generateFont(parameter);

            generator.dispose();
        }

        public void finishLoading() {
            manager.finishLoading();

            // Get loaded assets
            playerShip = manager.get("player_ship.png", Texture.class);
            enemyShip = manager.get("enemy_ship.png", Texture.class);
            background = manager.get("space_background.png", Texture.class);
            laser = manager.get("laser.png", Texture.class);
            explosion = manager.get("explosion.png", Texture.class);
            star = manager.get("star.png", Texture.class);

            laserSound = manager.get("laser.ogg", Sound.class);
            explosionSound = manager.get("explosion.ogg", Sound.class);
            upgradeSound = manager.get("upgrade.ogg", Sound.class);
        }

        public void dispose() {
            manager.dispose();
            font.dispose();
            titleFont.dispose();
        }
}
