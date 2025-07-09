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

public class MainScene implements Screen {
    private Game game;
    private Skin skin;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Stage stage = new Stage();
    private ImageButton coinButton;
    private Label coinLabel;
    private int coins = 0;
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
    @Override
    public void show() {
        backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
        batch = new SpriteBatch();
        skin = new Skin();

        initButton();
        initMenu();
//        initBossSystem();
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

    /* private void initBossSystem() {
        // Текстура босса
        bossTexture = new Texture(Gdx.files.internal("boss.jpg")); // Добавьте файл boss.png в assets
        bossImage = new Image(bossTexture);
        bossImage.setVisible(false);
        bossImage.setSize(300, 300);
        bossImage.setPosition(
                Gdx.graphics.getWidth()/2 - bossImage.getWidth()/2,
                Gdx.graphics.getHeight()/2 - bossImage.getHeight()/2 + 100
        );
        stage.addActor(bossImage);

        // UI босса
        bossUI = new Table();
        bossUI.setBackground(new TextureRegionDrawable(new TextureRegion(createWhitePixel(Color.DARK_GRAY))));
        bossUI.setVisible(false);
        bossUI.setPosition(Gdx.graphics.getWidth()/2 - 150, 50);
        bossUI.setSize(300, 100);

        // Шкала здоровья босса
        ProgressBar.ProgressBarStyle healthBarStyle = new ProgressBar.ProgressBarStyle(
                skin.newDrawable("white", Color.RED),
                skin.newDrawable("white", Color.GREEN)
        );
        healthBarStyle.knobBefore = healthBarStyle.knob;
        bossHealthBar = new ProgressBar(0, bossMaxHp, 1, false, healthBarStyle);
        bossHealthBar.setValue(bossMaxHp);
        bossHealthBar.setSize(280, 30);

        // Таймер босса
        Label.LabelStyle bossLabelStyle = new Label.LabelStyle();
        bossLabelStyle.font = new BitmapFont();
        bossLabelStyle.fontColor = Color.WHITE;
        bossTimerLabel = new Label("Time: " + (int)bossTimeLeft, bossLabelStyle);

        bossUI.add(new Label("BOSS FIGHT!", bossLabelStyle)).colspan(2).row();
        bossUI.add(bossHealthBar).colspan(2).padBottom(5).row();
        bossUI.add(bossTimerLabel);

        stage.addActor(bossUI);
    }*/

    private void startBossFight() {
        game.setScreen(new BossFightScene());
//        bossActive = true;
//        bossMaxHp = 10 + 50 * playerLevel;
//        bossHp = bossMaxHp;
//        bossTimeLeft = 30f;
//        clicksToBoss = 0;
//
//        bossImage.setVisible(true);
//        bossUI.setVisible(true);
//        bossHealthBar.setRange(0, bossMaxHp);
//        bossHealthBar.setValue(bossHp);
//
//        // Анимация появления
//        bossImage.setColor(1, 1, 1, 0);
//        bossImage.addAction(Actions.fadeIn(1f));
//
//        // Запуск таймера босса
//        Timer.schedule(new Timer.Task() {
//            @Override
//            public void run() {
//                bossTimeLeft -= 1f;
//                bossTimerLabel.setText("Time: " + (int)bossTimeLeft);
//
//                if (bossTimeLeft <= 0) {
//                    endBossFight(false);
//                }
//            }
//        }, 0, 1, (int)bossTimeLeft);
    }

    private void endBossFight(boolean victory) {
        bossActive = false;
        bossImage.setVisible(false);
        bossUI.setVisible(false);

        if (victory) {
            int reward = 50 * playerLevel;
            coins += reward;
            coinLabel.setText("Coins: " + coins);

            // Показать сообщение о победе
            showMessage("Win! +" + reward + " money");
            playerLevel++;
        } else {
            showMessage("Boss leave!");
        }
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

        if (bossActive) {
            bossHealthBar.setValue(bossHp);
        }

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

        TextButton button1 = new TextButton("New Game", skin);
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
                coinLabel.setText("Coins: " + coins);

                // Система вызова босса
                if (!bossActive) {
                    clicksToBoss++;
                    if (clicksToBoss >= BOSS_TRIGGER_CLICKS || Math.random() < 0.05) {
                        startBossFight();
                    }
                } else {
                    // Урон по боссу
                    bossHp -= 1 + playerLevel/5;
                    if (bossHp <= 0) {
                        endBossFight(true);
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