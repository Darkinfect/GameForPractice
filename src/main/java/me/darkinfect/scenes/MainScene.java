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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.Align;

public class MainScene implements Screen {
    private Skin skin;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Label BossFigth;
    private final Stage stage = new Stage();
    private ImageButton coinButton;
    private Label coinLabel;
    private int Coins = 0;
    private ImageButton menuButton;
    private Table menu;
    private boolean isMenuVisible = false;

    @Override
    public void show() {
        backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
        batch = new SpriteBatch();
        skin = new Skin();
        initbutton();
        initMenu();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.GOLD;

        coinLabel = new Label("Coins: " + Coins, labelStyle);
        coinLabel.setPosition(Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight() - 50);

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
        skin.dispose();
    }
    private void initMenu(){
        skin= new Skin(Gdx.files.internal("uiskin.json"));
        Texture buttonTexture = new Texture(Gdx.files.internal("menu.jpg"));
        ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.imageUp = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        skin.add("menuButtonStyle", imageButtonStyle);
        menuButton = new ImageButton(skin,"menuButtonStyle");
        menuButton.setPosition(20, Gdx.graphics.getHeight() - 70);
        menuButton.setSize(50, 50);

        // Создание выпадающего меню
        menu = new Table(skin);
        menu.setBackground(new TextureRegionDrawable(new TextureRegion(createWhitePixel())));
        menu.align(Align.topLeft);
        menu.pad(10);
        menu.setPosition(20, Gdx.graphics.getHeight() - 150);
        menu.setVisible(false);


        TextButton button1 = new TextButton("New Game", skin);
        TextButton button2 = new TextButton("Settings", skin);
        TextButton button3 = new TextButton("Exit", skin);

        menu.add(button1).padBottom(10).width(200).height(50).row();
        menu.add(button2).padBottom(10).width(200).height(50).row();
        menu.add(button3).width(200).height(50);

        // Обработчик клика по кнопке "гамбургер"
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isMenuVisible = !isMenuVisible;
                menu.setVisible(isMenuVisible);
            }
        });

        // Обработчики для кнопок меню
        button1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("New Game clicked!");
            }
        });

        button3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(menuButton);
        stage.addActor(menu);

    }
    private void initbutton(){
        Texture coinTexture = new Texture(Gdx.files.internal("clickbutton.jpg"));
        skin.add("coin", coinTexture);

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
    public void updateCoinCounter() {
        coinLabel.setText("Coins: " + Coins);
    }
    private Texture createWhitePixel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
