package me.darkinfect.scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.awt.*;

public class MainMenu implements Screen {
    private Stage stage;
    private final Game game;
    private Skin skin;
    private final int WINDOW_WIDTH = 400;
    private final int WINDOW_HEIGHT = 700;


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

        // Стиль для кнопок
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("default");
        buttonStyle.fontColor = Color.WHITE;

        // Стиль для заголовка
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = skin.getFont("default");
        titleStyle.fontColor = Color.GOLD;

        // Главная таблица
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Заголовок игры
        Label titleLabel = new Label("JUST A HUMAN", titleStyle);
        titleLabel.setFontScale(2f);

        // Кнопки меню
        TextButton playButton = new TextButton("Start Game", buttonStyle);
        TextButton settingsButton = new TextButton("Settings", buttonStyle);
        TextButton exitButton = new TextButton("Exit", buttonStyle);

        // Устанавливаем размеры кнопок
        playButton.getLabel().setFontScale(1.5f);
        settingsButton.getLabel().setFontScale(1.5f);
        exitButton.getLabel().setFontScale(1.5f);

        // Добавляем элементы в таблицу
        table.add(titleLabel).padBottom(50).row();
        table.add(playButton).width(200).height(60).padBottom(20).row();
        table.add(settingsButton).width(200).height(60).padBottom(20).row();
        table.add(exitButton).width(200).height(60);

        // Обработчики событий
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainScene()); // Переход на игровой экран
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game)); // Переход на экран настроек
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit(); // Выход из игры
            }
        });

        stage.addActor(table);
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
        float aspectRatio = (float)WINDOW_WIDTH/WINDOW_HEIGHT;
        float screenAspectRatio = (float)width/height;

        if (screenAspectRatio > aspectRatio) {
            int viewportWidth = (int)(height * aspectRatio);
            int viewportX = (width - viewportWidth) / 2;
            Gdx.gl.glViewport(viewportX, 0, viewportWidth, height);
        } else {
            int viewportHeight = (int)(width / aspectRatio);
            int viewportY = (height - viewportHeight) / 2;
            Gdx.gl.glViewport(0, viewportY, width, viewportHeight);
        }

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
