package me.darkinfect;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ClickerGame extends Game{
    private SpriteBatch batch;
    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MainScreen(this));
    }
    @Override
    public void dispose() {
        batch.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
