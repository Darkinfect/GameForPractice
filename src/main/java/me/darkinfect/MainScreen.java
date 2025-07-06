package me.darkinfect;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class MainScreen implements Screen {
    private final ClickerGame game;
    private final SpriteBatch batch;
    private final BitmapFont font;

    public MainScreen(ClickerGame game) {
        this.game = game;
        this.batch = game.getBatch();
        this.font = new BitmapFont();
    }

    @Override
    public void show() {

        Gdx.app.log("MainMenuScreen", "show() called");
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        batch.begin();
        font.draw(batch, "Main Menu", 100, 100);
        batch.end();

//
//        if (Gdx.input.justTouched()) {
//            game.setScreen(new GameScreen(game));
//        }
    }

    @Override
    public void dispose() {
        font.dispose();
    }


    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
