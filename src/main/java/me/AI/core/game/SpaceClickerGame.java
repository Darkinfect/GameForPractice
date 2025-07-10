package me.AI.core.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.AI.core.game.utils.Assets;

public class SpaceClickerGame extends Game{
    public SpriteBatch batch;
    public Assets assets;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new Assets();
        assets.load();

        // Wait for assets to load
        assets.manager.finishLoading();

        // Start with GameScreen
        setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.2f, 1); // Dark space background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
    }
}
