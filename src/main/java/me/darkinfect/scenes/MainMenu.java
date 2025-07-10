package me.darkinfect.scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.awt.*;

public class MainMenu implements Screen {
    private Stage stage;
    private final Game game;
    private Skin skin;
    private final int WINDOW_WIDTH = 400;
    private final int WINDOW_HEIGHT = 700;
    private com.badlogic.gdx.scenes.scene2d.ui.Image bgImage;
    private Table panelTable;
    private static MainMenu instance;

    public static MainMenu getInstance(Game game) {
        if (instance == null) {
            instance = new MainMenu(game);
            return instance;
        }
        return instance;
    }

    public MainMenu(Game game){
        this.game = game;
    }
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Создаем базовый скин
        skin = new Skin();
        skin.add("default", new BitmapFont());

        // Градиентный фон
        Pixmap bgPixmap = new Pixmap(1920, 1080, Pixmap.Format.RGBA8888); // большой размер для любого экрана
        for (int y = 0; y < 1080; y++) {
            float t = (float)y / 1080f;
            bgPixmap.setColor(0.12f + 0.18f * t, 0.12f + 0.18f * t, 0.25f + 0.25f * t, 1f);
            bgPixmap.drawLine(0, y, 1920, y);
        }
        Texture bgTexture = new Texture(bgPixmap);
        bgPixmap.dispose();
        bgImage = new com.badlogic.gdx.scenes.scene2d.ui.Image(bgTexture);
        bgImage.setFillParent(true);

        // Полупрозрачная панель
        Pixmap panelPixmap = new Pixmap(420, 420, Pixmap.Format.RGBA8888);
        panelPixmap.setColor(0.18f, 0.18f, 0.28f, 0.92f);
        panelPixmap.fillRectangle(0, 0, 420, 420);
        panelPixmap.setColor(0.25f, 0.25f, 0.35f, 0.92f);
        panelPixmap.fillCircle(30, 30, 30);
        panelPixmap.fillCircle(390, 30, 30);
        panelPixmap.fillCircle(30, 390, 30);
        panelPixmap.fillCircle(390, 390, 30);
        Texture panelTexture = new Texture(panelPixmap);
        panelPixmap.dispose();

        // Стиль для кнопок
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("default");
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = new Color(0.8f, 0.9f, 1f, 1f);
        buttonStyle.downFontColor = new Color(0.6f, 0.7f, 1f, 1f);
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(panelTexture));
        buttonStyle.over = new TextureRegionDrawable(new TextureRegion(panelTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(panelTexture));

        // Стиль для заголовка
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = skin.getFont("default");
        titleStyle.fontColor = Color.GOLD;

        // Главная таблица
        Table table = new Table();
        table.center();
        table.defaults().padBottom(10);

        // Заголовок игры с glow-эффектом
        Label titleLabel = new Label("JUST A HUMAN", titleStyle);
        titleLabel.setFontScale(2.5f);
        titleLabel.setAlignment(Align.center);
        Label shadowLabel = new Label("JUST A HUMAN", titleStyle);
        shadowLabel.setFontScale(2.5f);
        shadowLabel.setColor(0,0,0,0.4f);
        shadowLabel.setAlignment(Align.center);

        // Кнопки меню
        TextButton playButton = new TextButton("Start Game", buttonStyle);
        TextButton settingsButton = new TextButton("Settings", buttonStyle);
        TextButton exitButton = new TextButton("Exit", buttonStyle);
        playButton.getLabel().setFontScale(1.5f);
        settingsButton.getLabel().setFontScale(1.5f);
        exitButton.getLabel().setFontScale(1.5f);

        // Добавляем элементы в таблицу
        table.add(shadowLabel).padBottom(54).row();
        table.add(titleLabel).padBottom(50).row();
        table.add(playButton).width(260).height(70).padBottom(28).row();
        table.add(settingsButton).width(260).height(70).padBottom(28).row();
        table.add(exitButton).width(260).height(70);

        // Обработчики событий
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(MainScene.getIntsance(game));
            }
        });
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game));
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Панель-обёртка для меню
        panelTable = new Table();
        panelTable.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        panelTable.add(table).expand().fill();
        panelTable.center();
        panelTable.setFillParent(true);

        // Добавляем фон и панель
        stage.addActor(bgImage);
        stage.addActor(panelTable);
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(v);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
