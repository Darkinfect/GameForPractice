package me.darkinfect.scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import me.darkinfect.Main;

public class MainScene implements Screen {
    private static MainScene intsance;
    private Game game;
    private Skin skin;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Stage stage = new Stage();
    private ImageButton coinButton;
    private Label coinLabel;
    private static int coins = 0;
    private ImageButton menuButton;
    private Table menu;
    private boolean isMenuVisible = false;

    // Босс-система
    private boolean bossActive = false;
    private int bossHp = 0;
    private int bossMaxHp = 100;
    private float bossTimeLeft = 30f;
    private ProgressBar bossHealthBar;
    private Label bossTimerLabel;
    private Table bossUI;
    private Texture bossTexture;
    private Image bossImage;
    private int clicksToBoss = 0;
    private final int BOSS_TRIGGER_CLICKS = 150;
    private int playerLevel = 1;
    public MainScene(Game game){
        this.game = game;
    }
    public static void addCoin(int count){
        coins +=count;
        return;
    }

    public static MainScene getIntsance(Game game1){
        if(intsance == null){
            intsance = new MainScene(game1);
            return intsance;
        }
        return intsance;
    }
    public static MainScene getIntsance1(){
        return intsance;
    }
    @Override
    public void show() {
        backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
        batch = new SpriteBatch();
        skin = new Skin();

        initButton();
        initMenu();
        initUI();

        Gdx.input.setInputProcessor(stage);
    }

    private void initUI() {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.GOLD;

        coinLabel = new Label("Coins: " + coins, labelStyle);
        coinLabel.setPosition(Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight() - 50);
        stage.addActor(coinLabel);
    }

    private void startBossFight() {
        coinLabel.clear();
        game.setScreen(new BossFightScene(game));
    }

    private void showMessage(String text) {
        Label.LabelStyle messageStyle = new Label.LabelStyle();
        messageStyle.font = new BitmapFont();
        messageStyle.fontColor = Color.YELLOW;

        Label message = new Label(text, messageStyle);
        message.setPosition(
                Gdx.graphics.getWidth()/2 - message.getWidth()/2,
                Gdx.graphics.getHeight()/2
        );
        stage.addActor(message);

        // Автоматическое удаление сообщения через 3 секунды
        message.addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.removeActor()
        ));
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

    private void initMenu(){
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Texture buttonTexture = new Texture(Gdx.files.internal("menu.jpg"));
        ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.imageUp = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        skin.add("menuButtonStyle", imageButtonStyle);
        menuButton = new ImageButton(skin,"menuButtonStyle");
        menuButton.setPosition(20, Gdx.graphics.getHeight() - 70);
        menuButton.setSize(50, 50);

        menu = new Table(skin);
        menu.setBackground(new TextureRegionDrawable(new TextureRegion(createWhitePixel(Color.GRAY))));
        menu.align(Align.topLeft);
        menu.pad(10);
        menu.setPosition(20, Gdx.graphics.getHeight() - 150);
        menu.setVisible(false);

        TextButton button1 = new TextButton("Upgrades", skin);
        TextButton button2 = new TextButton("Settings", skin);
        TextButton button3 = new TextButton("Exit", skin);

        menu.add(button1).padBottom(10).width(200).height(50).row();
        menu.add(button2).padBottom(10).width(200).height(50).row();
        menu.add(button3).width(200).height(50);

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isMenuVisible = !isMenuVisible;
                menu.setVisible(isMenuVisible);
            }
        });
        button1.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                if(coins < 10){
                    return;
                }
                coins -= 10;
                BossFightScene.addplayerlevel(1);

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

    private void initButton(){
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
                coins++;
                updateLabelCoin();

                // Система вызова босса
                if (!bossActive) {
                    clicksToBoss++;
                    if (clicksToBoss >= BOSS_TRIGGER_CLICKS || Math.random() < 0.05) {
                        startBossFight();
                    }
                }
            }
        });

        stage.addActor(coinButton);
    }

    private Texture createWhitePixel(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
    public void updateLabelCoin(){
        coinLabel.setText("Coins: " + coins);
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
        bossTexture.dispose();
    }
}