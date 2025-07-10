package me.darkinfect.scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SettingsScreen implements Screen {
    private Game game;
    private Stage stage;
    private Skin skin;
    private static Preferences prefs;
    private static final String PREFS_NAME = "GameSettings";
    private static final String MUSIC_ENABLED_KEY = "musicEnabled";
    private static final String SOUND_ENABLED_KEY = "soundEnabled";
    private static final String MUSIC_VOLUME_KEY = "musicVolume";
    private static final String SOUND_VOLUME_KEY = "soundVolume";
    private static final String MASTER_VOLUME_KEY = "masterVolume";
    private static boolean musicEnabled = true;
    private static boolean soundEnabled = true;
    private static float musicVolume = 0.5f;
    private static float soundVolume = 0.5f;
    private static float masterVolume = 1.0f;
    private com.badlogic.gdx.scenes.scene2d.ui.Image bgImage;
    private Table panelTable;
    private com.badlogic.gdx.audio.Sound clickSound;

    private static SettingsScreen instance;

    public static SettingsScreen getInstance(Game game) {
        if (instance == null) {
            instance = new SettingsScreen(game);
            return instance;
        }
        return instance;
    }

    public SettingsScreen(Game game) {
        this.game = game;
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        loadSettings();
    }

    private void loadSettings() {
        musicEnabled = prefs.getBoolean(MUSIC_ENABLED_KEY, true);
        soundEnabled = prefs.getBoolean(SOUND_ENABLED_KEY, true);
        musicVolume = prefs.getFloat(MUSIC_VOLUME_KEY, 0.5f);
        soundVolume = prefs.getFloat(SOUND_VOLUME_KEY, 0.5f);
        masterVolume = prefs.getFloat(MASTER_VOLUME_KEY, 1.0f);
    }

    private void saveSettings() {
        prefs.putBoolean(MUSIC_ENABLED_KEY, musicEnabled);
        prefs.putBoolean(SOUND_ENABLED_KEY, soundEnabled);
        prefs.putFloat(MUSIC_VOLUME_KEY, musicVolume);
        prefs.putFloat(SOUND_VOLUME_KEY, soundVolume);
        prefs.putFloat(MASTER_VOLUME_KEY, masterVolume);
        prefs.flush();
    }

    public static boolean isMusicEnabled() {
        return musicEnabled;
    }

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static float getMusicVolume() {
        return musicVolume;
    }

    public static float getSoundVolume() {
        return soundVolume;
    }

    public static float getMasterVolume() {
        return masterVolume;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // Gradient background
        Pixmap bgPixmap = new Pixmap(1920, 1080, Pixmap.Format.RGBA8888);
        for (int y = 0; y < 1080; y++) {
            float t = (float)y / 1080f;
            bgPixmap.setColor(0.12f + 0.18f * t, 0.12f + 0.18f * t, 0.25f + 0.25f * t, 1f);
            bgPixmap.drawLine(0, y, 1920, y);
        }
        Texture bgTexture = new Texture(bgPixmap);
        bgPixmap.dispose();
        bgImage = new com.badlogic.gdx.scenes.scene2d.ui.Image(bgTexture);
        bgImage.setFillParent(true);


        // Semi-transparent panel
        Pixmap panelPixmap = new Pixmap(420, 520, Pixmap.Format.RGBA8888);
        panelPixmap.setColor(0.18f, 0.18f, 0.28f, 0.92f);
        panelPixmap.fillRectangle(0, 0, 420, 520);
        panelPixmap.setColor(0.25f, 0.25f, 0.35f, 0.92f);
        panelPixmap.fillCircle(30, 30, 30);
        panelPixmap.fillCircle(390, 30, 30);
        panelPixmap.fillCircle(30, 490, 30);
        panelPixmap.fillCircle(390, 490, 30);
        Texture panelTexture = new Texture(panelPixmap);
        panelPixmap.dispose();

        // Main table for settings
        Table table = new Table(skin);
        table.defaults().pad(10).width(300);

        // Title
        Label titleLabel = new Label("Settings", skin, "default");
        titleLabel.setFontScale(2.0f);
        titleLabel.setAlignment(Align.center);
        table.add(titleLabel).colspan(2).padBottom(20).row();

        // Master volume
        Label masterVolumeLabel = new Label("Master Volume: " + (int)(masterVolume * 100) + "%", skin);
        Slider masterVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin, "default-horizontal");
        masterVolumeSlider.setValue(masterVolume);
        masterVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                masterVolume = masterVolumeSlider.getValue();
                masterVolumeLabel.setText("Master Volume: " + (int)(masterVolume * 100) + "%");
                saveSettings();
            }
        });
        table.add(masterVolumeLabel).padBottom(5).row();
        table.add(masterVolumeSlider).padBottom(15).row();

        // Music volume
        Label musicVolumeLabel = new Label("Music Volume: " + (int)(musicVolume * 100) + "%", skin);
        Slider musicVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin, "default-horizontal");
        musicVolumeSlider.setValue(musicVolume);
        musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicVolume = musicVolumeSlider.getValue();
                musicVolumeLabel.setText("Music Volume: " + (int)(musicVolume * 100) + "%");
                saveSettings();
            }
        });
        table.add(musicVolumeLabel).padBottom(5).row();
        table.add(musicVolumeSlider).padBottom(15).row();

        // Sound volume
        Label soundVolumeLabel = new Label("Sound Volume: " + (int)(soundVolume * 100) + "%", skin);
        Slider soundVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin, "default-horizontal");
        soundVolumeSlider.setValue(soundVolume);
        soundVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundVolume = soundVolumeSlider.getValue();
                soundVolumeLabel.setText("Sound Volume: " + (int)(soundVolume * 100) + "%");
                saveSettings();
            }
        });
        table.add(soundVolumeLabel).padBottom(5).row();
        table.add(soundVolumeSlider).padBottom(15).row();

        // Music checkbox
        CheckBox musicCheckBox = new CheckBox("Enable Music", skin);
        musicCheckBox.setChecked(musicEnabled);
        musicCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicEnabled = musicCheckBox.isChecked();
                saveSettings();
            }
        });
        table.add(musicCheckBox).padBottom(15).row();

        // Sound checkbox
        CheckBox soundCheckBox = new CheckBox("Enable Sounds", skin);
        soundCheckBox.setChecked(soundEnabled);
        soundCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundEnabled = soundCheckBox.isChecked();
                saveSettings();
            }
        });
        table.add(soundCheckBox).padBottom(15).row();


        // Back button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (MainScene.getIntsance1() != null) {
                    game.setScreen(MainScene.getIntsance1());
                    return;
                }
                game.setScreen(MainMenu.getInstance(game));
            }
        });
        table.add(backButton).height(50).row();

        // Panel wrapper
        panelTable = new Table();
        panelTable.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        panelTable.add(table).expand().fill();
        panelTable.setSize(420, 520);
        panelTable.setPosition(
                (Gdx.graphics.getWidth() - 420) / 2,
                (Gdx.graphics.getHeight() - 520) / 2
        );

        // Add actors to stage
        stage.addActor(bgImage);
        stage.addActor(panelTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f,  1);
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
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
