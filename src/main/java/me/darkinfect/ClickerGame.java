package me.darkinfect;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.darkinfect.scenes.MainScene;

public class ClickerGame extends Game{
    private SpriteBatch batch;
    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MainScene());
    }
    @Override
    public void dispose() {
        batch.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
