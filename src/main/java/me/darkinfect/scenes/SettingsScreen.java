package me.darkinfect.scenes;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SettingsScreen implements Screen {
    private final Game game;
    private Stage stage;
    private Skin skin;
    private Table table;

    // Настройки (можно сохранять в Preferences)
    private boolean soundEnabled = true;
    private boolean vibrationEnabled = true;
    private float musicVolume = 0.7f;
    private float soundVolume = 0.8f;

    public SettingsScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Создаем скин
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        skin.add("default", new BitmapFont());

        // Стиль для заголовка
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = skin.getFont("default");
        titleStyle.fontColor = Color.WHITE;

        // Стиль для кнопок
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
//        buttonStyle.font = skin.getFont("default");
        buttonStyle.fontColor = Color.WHITE;

        // Создаем таблицу для компоновки
        table = new Table();
        table.setFillParent(true);
        table.pad(20);

        // Заголовок
        Label title = new Label("SETTINGS", titleStyle);
        title.setFontScale(1.8f);
        table.add(title).colspan(2).padBottom(30).row();

        // 1. Переключатель звука
        final CheckBox soundCheckbox = new CheckBox(" SOUND", skin);
        soundCheckbox.setChecked(soundEnabled);
        soundCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundEnabled = soundCheckbox.isChecked();
                // Здесь можно добавить логику включения/выключения звука
            }
        });

        // 2. Переключатель вибрации
        final CheckBox vibrationCheckbox = new CheckBox(" Vibration", skin);
        vibrationCheckbox.setChecked(vibrationEnabled);
        vibrationCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                vibrationEnabled = vibrationCheckbox.isChecked();
            }
        });

        // 3. Громкость музыки
        Label musicLabel = new Label("Громкость музыки:", skin);
        final Slider musicSlider = new Slider(0, 1, 0.1f, false, skin);
        musicSlider.setValue(musicVolume);
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicVolume = musicSlider.getValue();
                // Здесь можно обновить громкость музыки
            }
        });

        // 4. Громкость звуков
        Label soundLabel = new Label("Sound Loud:", skin);
        final Slider soundSlider = new Slider(0, 1, 0.1f, false, skin);
        soundSlider.setValue(soundVolume);
        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundVolume = soundSlider.getValue();
                // Здесь можно обновить громкость звуков
            }
        });

        // Кнопка "Назад"
        TextButton backButton = new TextButton("Back", buttonStyle);
        backButton.getLabel().setFontScale(1.2f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenu(game));
            }
        });

        // Добавляем элементы в таблицу
        table.add(soundCheckbox).left().padBottom(15).colspan(2).row();
        table.add(vibrationCheckbox).left().padBottom(15).colspan(2).row();

        table.add(musicLabel).left().padBottom(5);
        table.add(musicSlider).fillX().padBottom(15).row();

        table.add(soundLabel).left().padBottom(5);
        table.add(soundSlider).fillX().padBottom(30).row();

        table.add(backButton).colspan(2).width(150).height(50);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
    public void hide() {
        // При закрытии экрана можно сохранить настройки
        saveSettings();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    private void saveSettings() {
        // Сохранение настроек в Preferences
        Preferences prefs = Gdx.app.getPreferences("ClickerSettings");
        prefs.putBoolean("soundEnabled", soundEnabled);
        prefs.putBoolean("vibrationEnabled", vibrationEnabled);
        prefs.putFloat("musicVolume", musicVolume);
        prefs.putFloat("soundVolume", soundVolume);
        prefs.flush();
    }

    private void loadSettings() {
        // Загрузка настроек из Preferences
        Preferences prefs = Gdx.app.getPreferences("ClickerSettings");
        soundEnabled = prefs.getBoolean("soundEnabled", true);
        vibrationEnabled = prefs.getBoolean("vibrationEnabled", true);
        musicVolume = prefs.getFloat("musicVolume", 0.7f);
        soundVolume = prefs.getFloat("soundVolume", 0.8f);
    }
}