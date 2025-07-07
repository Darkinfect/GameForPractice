package me.darkinfect.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Pixmap.Format;

public class MainScene implements Screen {
    private Skin skin;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Label BossFigth;
    private Stage stage = new Stage();
    private ImageButton coinButton;
    private Label coinLabel;
    private int Coins = 0;

    @Override
    public void show() {
        backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
        batch = new SpriteBatch();
        skin = new Skin();
        initbutton();
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont(); // Можно заменить на свой шрифт
        labelStyle.fontColor = Color.GOLD;

        // Инициализируем Label
        coinLabel = new Label("Coins: " + Coins, labelStyle);
        coinLabel.setPosition(20, Gdx.graphics.getHeight() - 50); // Позиция в верхнем левом углу

        stage.addActor(coinLabel);
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Отрисовка сцены с кнопкой
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        stage.dispose();
        skin.dispose(); // Не забываем освободить skin
    }
    private void initbutton(){
        Pixmap pixmap = new Pixmap(Gdx.files.internal("coin.png"));
        Format format = pixmap.getFormat();
        if (format != Pixmap.Format.RGBA8888) {
            Pixmap newPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
            newPixmap.drawPixmap(pixmap, 0, 0);
            pixmap = newPixmap;
        }
        Texture coinTexture = new Texture(Gdx.files.internal("coin.png"),true);
        coinTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        skin.add("coin", coinTexture);
        pixmap.dispose();

        ImageButton.ImageButtonStyle coinButtonStyle = new ImageButton.ImageButtonStyle();
        coinButtonStyle.imageUp = new TextureRegionDrawable(new TextureRegion(skin.get("coin", Texture.class)));
        coinButtonStyle.imageDown = new TextureRegionDrawable(new TextureRegion(skin.get("coin", Texture.class)));

        coinButton = new ImageButton(coinButtonStyle);

        float buttonSize = Gdx.graphics.getHeight() * 0.1f;
        coinButton.setSize(buttonSize, buttonSize);

        float x = (Gdx.graphics.getWidth() - buttonSize) / 2;
        float y = (Gdx.graphics.getHeight() / 2 - buttonSize) / 2;
        coinButton.setPosition(x, y);


        coinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Coin button clicked!");
                Coins++;
                updateCoinCounter();
            }
        });


        stage.addActor(coinButton);
    }
    // Функция для обновления счётчика
    public void updateCoinCounter() {
        coinLabel.setText("Coins: " + Coins);
    }
}
