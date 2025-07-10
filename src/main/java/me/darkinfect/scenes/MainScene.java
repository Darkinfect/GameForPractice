package me.darkinfect.scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import java.util.ArrayList;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MainScene implements Screen {
    private static MainScene intsance;
    private Game game;
    private Skin skin;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Stage stage = new Stage();
    private ImageButton coinButton;
    private Label coinLabel;
    private Label bossClicksLabel;
    public static int coins = 0;
    private ImageButton menuButton;
    private Table menu;
    private boolean isMenuVisible = false;
    private Music backgroundMusic;


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
    public int playerLevel = 1;
    public int minionStage = 0; // <-- добавлено публичное поле
    // --- Переменные для апгрейдов ---
    public int upgradeDamage = 1;
    public int upgradeMaxHp = 5;
    private int upgradeCoinsPerClick = 1;
    private int upgradePassiveIncome = 0;
    private float passiveIncomeTimer = 0f;
    private Table upgradeMenu;
    private boolean isUpgradeMenuVisible = false;
    private TextButton upgradeButton;
    private Label upgradeResultLabel;
    private Table upgradesSubMenu;
    private boolean isUpgradesSubMenuVisible = false;
    private static ArrayList<Achievement> achievements = new ArrayList<>();
    private static int totalClicks = 0;
    private static int upgradesBought = 0;
    public static int bossesKilled = 0;
    // --- Переменные для ачивок ---
    private ParticleEffect clickEffect;
    private float effectStartTime = -1f;
    private boolean isEffectActive = false;
    private Sound clickSound;
    private Sound achievementSound;
    private Image aboveButtonImage;
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
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        batch = new SpriteBatch();
        skin = new Skin();

        // --- Инициализация ачивок (только при первом запуске) ---
        if (achievements.isEmpty()) {
            achievements.add(new Achievement("first_click", "First Click", "Make your first click", Achievement.AchievementType.CLICKS, 1, 10));
            achievements.add(new Achievement("click_master", "Click Master", "Make 1000 clicks", Achievement.AchievementType.CLICKS, 1000, 100));
            achievements.add(new Achievement("click_expert", "Click Expert", "Make 5000 clicks", Achievement.AchievementType.CLICKS, 5000, 500));
            achievements.add(new Achievement("boss_slayer", "Boss Slayer", "Defeat your first boss", Achievement.AchievementType.BOSSES, 1, 200));
            achievements.add(new Achievement("boss_master", "Boss Master", "Defeat 5 bosses", Achievement.AchievementType.BOSSES, 5, 1000));
            achievements.add(new Achievement("upgrade_fan", "Upgrade Fan", "Buy 5 upgrades", Achievement.AchievementType.UPGRADES, 5, 150));
            achievements.add(new Achievement("upgrade_master", "Upgrade Master", "Buy 20 upgrades", Achievement.AchievementType.UPGRADES, 20, 500));
            achievements.add(new Achievement("wealthy", "Wealthy", "Accumulate 1000 coins", Achievement.AchievementType.COINS, 1000, 200));
            achievements.add(new Achievement("millionaire", "Millionaire", "Accumulate 10000 coins", Achievement.AchievementType.COINS, 10000, 2000));
            achievements.add(new Achievement("speed_clicker", "Speed Clicker", "Make 100 clicks in 10 seconds", Achievement.AchievementType.SPECIAL, 100, 300));
            achievements.add(new Achievement("survivor", "Survivor", "Defeat a boss with 1 HP", Achievement.AchievementType.SPECIAL, 1, 500));
        }
        // Initialize particles
        clickEffect = new ParticleEffect();
        try {
            clickEffect.load(Gdx.files.internal("coin_spark.p"), Gdx.files.internal(""));
            clickEffect.allowCompletion();
            Gdx.app.log("MainScene", "Эффект частиц успешно загружен");
        } catch (Exception e) {
            Gdx.app.error("MainScene", "Не удалось загрузить эффект частиц: " + e.getMessage(), e);
            clickEffect = null;
        }

        // Initialize sound
        try {
            clickSound = Gdx.audio.newSound(Gdx.files.internal("coin_click.wav"));
        } catch (Exception e) {
            Gdx.app.log("MainScene", "Failed to load coin_click.wav, sound disabled", e);
            clickSound = null;
        }
        
        // Initialize achievement sound
        try {
            achievementSound = Gdx.audio.newSound(Gdx.files.internal("achievement.wav"));
        } catch (Exception e) {
            Gdx.app.log("MainScene", "Failed to load achievement.wav, sound disabled", e);
            achievementSound = null;
        }
        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_music.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(SettingsScreen.getMusicVolume() * SettingsScreen.getMasterVolume());
            if (SettingsScreen.isMusicEnabled()) {
                backgroundMusic.play();
            }
            Gdx.app.log("MainScene", "Фоновая музыка успешно загружена");
        } catch (Exception e) {
            Gdx.app.error("MainScene", "Не удалось загрузить background_music.mp3, музыка отключена", e);
            backgroundMusic = null;
        }



        initButton();
        initMenu();
        initUpgradesSubMenu();
        initUI();

        Gdx.input.setInputProcessor(stage);
    }

    private void initUI() {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = new Color(0.294f, 0f, 0.509f, 1f); // Индиго цвет

        if (coinLabel != null) {
            coinLabel.remove(); // удаляем старый лейбл со сцены
        }
        coinLabel = new Label("Coins: " + coins, labelStyle);
        coinLabel.setFontScale(1.2f);
        coinLabel.setPosition(Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight() - 50);
        stage.addActor(coinLabel);
        if (bossClicksLabel != null) {
            bossClicksLabel.remove();
        }
        bossClicksLabel = new Label("To boss: " + (100 - (totalClicks % 100)), labelStyle);
        bossClicksLabel.setFontScale(1.2f);
        bossClicksLabel.setPosition(Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight() - 80);
        stage.addActor(bossClicksLabel);
        updateLabelCoin(); // сразу обновляем текст
    }

    private void startBossFight() {
        coinLabel.clear();
        game.setScreen(new BossFightScene(game, minionStage));
    }

    private void showMessage(String text) {
        Label.LabelStyle messageStyle = new Label.LabelStyle();
        messageStyle.font = new BitmapFont();
        messageStyle.fontColor = Color.GOLD; // Золотой цвет

        Label message = new Label(text, messageStyle);
        message.setFontScale(1.5f);
        
        // Позиционируем сообщение под кнопкой монет
        float buttonY = coinButton.getY();
        float messageY = buttonY - 80; // 80 пикселей под кнопкой
        message.setPosition(
                Gdx.graphics.getWidth()/2 - message.getWidth()/2,
                messageY
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
        // Проверяем нажатие пробела для заработка монет
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            coins += upgradeCoinsPerClick;
            updateLabelCoin();
            totalClicks++;
            checkAchievements();
            
            // Анимация кнопки
            coinButton.addAction(Actions.sequence(
                    Actions.scaleTo(1.2f, 1.2f, 0.1f),
                    Actions.scaleTo(1.0f, 1.0f, 0.1f)
            ));

            // Start particles
            if (clickEffect != null) {
                clickEffect.reset(); // Сбрасываем текущий эффект
                clickEffect.setPosition(coinButton.getX() + coinButton.getWidth() / 2, coinButton.getY() + coinButton.getHeight() / 2);
                clickEffect.start();
                isEffectActive = true;
                effectStartTime = Gdx.graphics.getRawDeltaTime(); // Обновляем время последнего клика
                Gdx.app.log("MainScene", "Эффект частиц запущен/перезапущен");
            }

            // Play sound
            if (SettingsScreen.isSoundEnabled() && clickSound != null) {
                clickSound.play(SettingsScreen.getSoundVolume());
            }

            // --- БОССФАЙТ ПО КЛИКАМ ---
            if (totalClicks > 0 && totalClicks % 100 == 0) {
                startBossFight();
            }
        }
        
        // пассивный доход
        if (upgradePassiveIncome > 0) {
            passiveIncomeTimer += delta;
            if (passiveIncomeTimer >= 1f) {
                coins += upgradePassiveIncome;
                updateLabelCoin();
                passiveIncomeTimer = 0f;
            }
        }
        // Update particles
        if (clickEffect != null && isEffectActive) {
            clickEffect.update(delta);
            float currentTime = Gdx.graphics.getRawDeltaTime();// Текущее время кадра
            if (currentTime - effectStartTime >= 1.0f) {
                isEffectActive = false;
                clickEffect.reset();
                Gdx.app.log("MainScene", "Эффект частиц завершен через 1 секунду после последнего клика");
            }
        }
        // Обновление громкости музыки
        if (backgroundMusic != null) {
            float volume = SettingsScreen.getMusicVolume() * SettingsScreen.getMasterVolume();
            backgroundMusic.setVolume(volume);
            if (SettingsScreen.isMusicEnabled() && !backgroundMusic.isPlaying()) {
                backgroundMusic.play();
            } else if (!SettingsScreen.isMusicEnabled() && backgroundMusic.isPlaying()) {
                backgroundMusic.stop();
            }
        }


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (clickEffect != null && isEffectActive) {
            clickEffect.draw(batch);
        }
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    private void initMenu(){
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Texture buttonTexture = new Texture(Gdx.files.internal("menu.png"));
        ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.imageUp = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        skin.add("menuButtonStyle", imageButtonStyle);
        menuButton = new ImageButton(skin,"menuButtonStyle");
        menuButton.setPosition(20, Gdx.graphics.getHeight() - 70);
        menuButton.setSize(70, 70);

        menu = new Table(skin);
        menu.setBackground(new TextureRegionDrawable(new TextureRegion(createWhitePixel(Color.GRAY))));
        menu.align(Align.topLeft);
        menu.pad(10);
        menu.setPosition(20, Gdx.graphics.getHeight() - 150);
        menu.setVisible(false);

        TextButton button1 = new TextButton("Upgrades", skin);
        TextButton button2 = new TextButton("Achievements", skin);
        TextButton button3 = new TextButton("Settings", skin);
        TextButton button4 = new TextButton("Exit", skin);

        menu.add(button1).padBottom(10).width(300).height(70).row();
        menu.add(button2).padBottom(10).width(300).height(70).row();
        menu.add(button3).padBottom(10).width(300).height(70).row();
        menu.add(button4).width(300).height(70);

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isMenuVisible = !isMenuVisible;
                menu.setVisible(isMenuVisible);
                if (isMenuVisible) {
                    menu.toFront();
                }
                if (isUpgradesSubMenuVisible) {
                    upgradesSubMenu.setVisible(false);
                    isUpgradesSubMenuVisible = false;
                }
            }
        });
        button1.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                isUpgradesSubMenuVisible = true;
                // Позиционируем апгрейд-меню справа от основного меню
                float menuX = menu.getX();
                float menuY = menu.getY();
                float menuWidth = menu.getWidth();
                upgradesSubMenu.setPosition(menuX + menuWidth + 10, menuY);
                upgradesSubMenu.setVisible(true);
                menu.setVisible(false);
                isMenuVisible = false;
            }
        });
        button2.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new AchievementsScreen(game, achievements));
                menu.setVisible(false);
                isMenuVisible = false;
            }
        });
        button3.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                // Settings logic here
                game.setScreen(SettingsScreen.getInstance(game));
                menu.setVisible(false);
                isMenuVisible = false;
            }
        });
        button4.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(menuButton);
        stage.addActor(menu);
    }

    private void initUpgradeMenu() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        upgradeMenu = new Table(skin);
        upgradeMenu.setBackground(new TextureRegionDrawable(new TextureRegion(createWhitePixel(Color.LIGHT_GRAY))));
        upgradeMenu.align(Align.topLeft);
        upgradeMenu.pad(10);
        upgradeMenu.setPosition(300, Gdx.graphics.getHeight() - 250);
        upgradeMenu.setVisible(false);

        Label infoLabel = new Label("Нажмите кнопку, чтобы получить случайное улучшение! (20 монет)", skin);
        upgradeResultLabel = new Label("", skin);
        TextButton getUpgradeBtn = new TextButton("Получить улучшение", skin);
        TextButton closeBtn = new TextButton("Закрыть", skin);

        upgradeMenu.add(infoLabel).padBottom(10).width(400).height(60).row();
        upgradeMenu.add(upgradeResultLabel).padBottom(10).width(400).height(60).row();
        upgradeMenu.add(getUpgradeBtn).padBottom(10).width(320).height(70).row();
        upgradeMenu.add(closeBtn).width(320).height(70);

        getUpgradeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (coins >= 20) {
                    coins -= 20;
                    int type = (int)(Math.random() * 4);
                    String result = "";
                    switch (type) {
                        case 0:
                            upgradeDamage++;
                            result = "Урон увеличен!";
                            break;
                        case 1:
                            upgradeMaxHp++;
                            result = "Максимальное HP увеличено!";
                            break;
                        case 2:
                            upgradeCoinsPerClick++;
                            result = "Больше коинов за клик!";
                            break;
                        case 3:
                            upgradePassiveIncome++;
                            result = "Пассивный доход увеличен!";
                            break;
                    }
                    updateLabelCoin();
                    upgradeResultLabel.setText(result);
                    upgradesBought++;
                    checkAchievements();
                } else {
                    upgradeResultLabel.setText("Недостаточно монет!");
                }
            }
        });
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isUpgradeMenuVisible = false;
                upgradeMenu.setVisible(false);
                upgradeResultLabel.setText("");
            }
        });
        stage.addActor(upgradeMenu);

        // Кнопка для открытия меню апгрейдов
        upgradeButton = new TextButton("Апгрейды", skin);
        upgradeButton.setSize(180, 70);
        upgradeButton.setPosition(120, Gdx.graphics.getHeight() - 70);
        upgradeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isUpgradeMenuVisible = !isUpgradeMenuVisible;
                upgradeMenu.setVisible(isUpgradeMenuVisible);
            }
        });
        stage.addActor(upgradeButton);
    }

    private void initButton(){
        Texture coinTexture = new Texture(Gdx.files.internal("button2.png"));
        skin.add("coin", coinTexture);

        // Добавляем изображение 400x400 выше кнопки на 100 пикселей

        ImageButton.ImageButtonStyle coinButtonStyle = new ImageButton.ImageButtonStyle();
        coinButtonStyle.imageUp = new TextureRegionDrawable(new TextureRegion(skin.get("coin", Texture.class)));
        coinButtonStyle.imageDown = new TextureRegionDrawable(new TextureRegion(skin.get("coin", Texture.class)));
        float buttonSize = Gdx.graphics.getHeight() * 0.2f;
        float x = (Gdx.graphics.getWidth() - buttonSize) / 2;
        float y = (Gdx.graphics.getHeight() / 2 - buttonSize) / 2;

        coinButton = new ImageButton(coinButtonStyle);

        coinButton.setSize(buttonSize, buttonSize);
        coinButton.setPosition(x, y);

        Texture aboveTexture = new Texture(Gdx.files.internal("button.jpg"));
        aboveButtonImage = new Image(aboveTexture);
        aboveButtonImage.setSize(400, 400);
        float aboveX = (Gdx.graphics.getWidth() - 400) / 2f;
        float aboveY = coinButton.getY() + 150 ; // выше кнопки на 100 пикселей
        aboveButtonImage.setPosition(aboveX, aboveY);
        stage.addActor(aboveButtonImage);
        coinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                coins += upgradeCoinsPerClick;
                updateLabelCoin();
                totalClicks++;
                checkAchievements();
                // Анимация кнопки
                coinButton.addAction(Actions.sequence(
                        Actions.scaleTo(1.2f, 1.2f, 0.1f),
                        Actions.scaleTo(1.0f, 1.0f, 0.1f)
                ));

                // Start particles
                if (clickEffect != null) {
                    clickEffect.reset(); // Сбрасываем текущий эффект
                    clickEffect.setPosition(coinButton.getX() + coinButton.getWidth() / 2, coinButton.getY() + coinButton.getHeight() / 2);
                    clickEffect.start();
                    isEffectActive = true;
                    effectStartTime = Gdx.graphics.getRawDeltaTime(); // Обновляем время последнего клика
                    Gdx.app.log("MainScene", "Эффект частиц запущен/перезапущен");
                }

                // Play sound
                if (SettingsScreen.isSoundEnabled() && clickSound != null) {
                    clickSound.play(SettingsScreen.getSoundVolume());
                }

                // --- БОССФАЙТ ПО КЛИКАМ ---
                if (totalClicks > 0 && totalClicks % 100 == 0) {
                    startBossFight();
                }
            }
        });

        stage.addActor(coinButton);

        // summonBossButton удалён, чтобы не было ошибки TextButtonStyle
    }

    private void initUpgradesSubMenu() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        upgradesSubMenu = new Table(skin);
        upgradesSubMenu.setBackground(new TextureRegionDrawable(new TextureRegion(createWhitePixel(Color.WHITE))));
        upgradesSubMenu.align(Align.topLeft);
        upgradesSubMenu.pad(10);
        upgradesSubMenu.setPosition(80, Gdx.graphics.getHeight() - 300);
        upgradesSubMenu.setVisible(false);

        TextButton dmgBtn = new TextButton("Upgrade Damage (10)", skin);
        TextButton hpBtn = new TextButton("Upgrade HP (10)", skin);
        TextButton coinBtn = new TextButton("Upgrade Coins per Click (15)", skin);
        TextButton passiveBtn = new TextButton("Upgrade Passive Income (20)", skin);
        TextButton backBtn = new TextButton("Back", skin);
        Label upgradeMsg = new Label("", skin);

        upgradesSubMenu.add(dmgBtn).padBottom(8).width(320).height(60).row();
        upgradesSubMenu.add(hpBtn).padBottom(8).width(320).height(60).row();
        upgradesSubMenu.add(coinBtn).padBottom(8).width(320).height(60).row();
        upgradesSubMenu.add(passiveBtn).padBottom(8).width(320).height(60).row();
        upgradesSubMenu.add(upgradeMsg).padBottom(8).width(320).height(40).row();
        upgradesSubMenu.add(backBtn).width(320).height(60);

        dmgBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (coins >= 10) {
                    coins -= 10;
                    upgradeDamage++;
                    upgradesBought++;
                    updateLabelCoin();
                    checkAchievements();
                    upgradeMsg.setText("Damage upgraded!");
                } else {
                    upgradeMsg.setText("Not enough coins!");
                }
            }
        });
        hpBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (coins >= 10) {
                    coins -= 10;
                    upgradeMaxHp++;
                    upgradesBought++;
                    updateLabelCoin();
                    checkAchievements();
                    upgradeMsg.setText("HP upgraded!");
                } else {
                    upgradeMsg.setText("Not enough coins!");
                }
            }
        });
        coinBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (coins >= 15) {
                    coins -= 15;
                    upgradeCoinsPerClick++;
                    upgradesBought++;
                    updateLabelCoin();
                    checkAchievements();
                    upgradeMsg.setText("Coins per click upgraded!");
                } else {
                    upgradeMsg.setText("Not enough coins!");
                }
            }
        });
        passiveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (coins >= 20) {
                    coins -= 20;
                    upgradePassiveIncome++;
                    upgradesBought++;
                    updateLabelCoin();
                    checkAchievements();
                    upgradeMsg.setText("Passive income upgraded!");
                } else {
                    upgradeMsg.setText("Not enough coins!");
                }
            }
        });
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isUpgradesSubMenuVisible = false;
                upgradesSubMenu.setVisible(false);
                isMenuVisible = true;
                menu.setVisible(true);
                upgradeMsg.setText("");
            }
        });
        stage.addActor(upgradesSubMenu);
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
        if (bossClicksLabel != null) {
            bossClicksLabel.setText("To boss: " + (100 - (totalClicks % 100)));
        }
    }
    private void checkAchievements() {
        for (Achievement a : achievements) {
            if (!a.isUnlocked()) {
                switch (a.getType()) {
                    case CLICKS:
                        a.updateProgress(totalClicks);
                        break;
                    case COINS:
                        a.updateProgress(coins);
                        break;
                    case BOSSES:
                        a.updateProgress(bossesKilled);
                        break;
                    case UPGRADES:
                        a.updateProgress(upgradesBought);
                        break;
                    case TIME:
                        // Время игры можно добавить позже
                        break;
                    case SPECIAL:
                        checkSpecialAchievements(a);
                        break;
                }
                
                if (a.isUnlocked()) {
                    unlockAchievement(a);
                }
            }
        }
    }
    
    private void checkSpecialAchievements(Achievement a) {
        switch (a.getId()) {
            case "speed_clicker":
                // Проверка скоростного кликера (нужно добавить логику)
                break;
            case "survivor":
                // Проверка выжившего (нужно добавить логику)
                break;
        }
    }
    
    private void unlockAchievement(Achievement a) {
        coins += a.getRewardCoins();
        updateLabelCoin();
        
        // Воспроизводим звук достижения
        if (SettingsScreen.isSoundEnabled() && achievementSound != null) {
            achievementSound.play(SettingsScreen.getSoundVolume() * 0.3f);
        }
        
        showMessage("Achievement unlocked: " + a.getName() + " (+" + a.getRewardCoins() + " coins)");
    }
    
    private void showAchievementsMenu() {
        // Создаем отдельное окно с достижениями
        Table achievementsWindow = new Table(skin);
        achievementsWindow.setBackground(new TextureRegionDrawable(new TextureRegion(createWhitePixel(Color.DARK_GRAY))));
        achievementsWindow.align(Align.topLeft);
        achievementsWindow.pad(30);
        achievementsWindow.setPosition(Gdx.graphics.getWidth()/2 - 450, Gdx.graphics.getHeight()/2 - 400);
        achievementsWindow.setSize(900, 800);
        
        // Заголовок окна
        Label titleLabel = new Label("ACHIEVEMENTS", skin);
        titleLabel.setFontScale(2.5f);
        titleLabel.setColor(Color.CYAN);
        achievementsWindow.add(titleLabel).padBottom(30).row();
        
        // Создаем скроллируемую область для достижений
        Table achievementsContainer = new Table(skin);
        achievementsContainer.align(Align.topLeft);
        
        int unlockedCount = 0;
        int totalCount = achievements.size();
        
        for (Achievement achievement : achievements) {
            if (achievement.isUnlocked()) {
                unlockedCount++;
            }
            
            // Создаем карточку для каждого достижения
            Table achievementCard = new Table(skin);
            Color cardColor = achievement.isUnlocked() ? new Color(0.2f, 0.8f, 0.2f, 0.3f) : new Color(0.3f, 0.3f, 0.3f, 0.3f);
            achievementCard.setBackground(new TextureRegionDrawable(new TextureRegion(createWhitePixel(cardColor))));
            achievementCard.pad(15);
            achievementCard.align(Align.topLeft);
            
            // Название достижения
            Label nameLabel = new Label(achievement.getName(), skin);
            nameLabel.setFontScale(1.3f);
            nameLabel.setColor(achievement.isUnlocked() ? Color.GOLD : Color.CYAN);
            
            // Описание
            Label descLabel = new Label(achievement.getDescription(), skin);
            descLabel.setFontScale(1.0f);
            descLabel.setColor(Color.WHITE);
            
            // Прогресс
            Label progressLabel = new Label(achievement.getProgressText(), skin);
            progressLabel.setFontScale(1.1f);
            progressLabel.setColor(achievement.isUnlocked() ? Color.GREEN : Color.YELLOW);
            
            // Награда
            Label rewardLabel = new Label("Reward: " + achievement.getRewardCoins() + " coins", skin);
            rewardLabel.setFontScale(1.0f);
            rewardLabel.setColor(Color.CYAN);
            
            // Добавляем элементы в карточку
            achievementCard.add(nameLabel).padBottom(8).row();
            achievementCard.add(descLabel).padBottom(8).row();
            achievementCard.add(progressLabel).padBottom(8).row();
            achievementCard.add(rewardLabel).padBottom(5).row();
            
            // Добавляем карточку в контейнер
            achievementsContainer.add(achievementCard).width(800).height(120).padBottom(10).row();
        }
        
        // Статистика
        Label statsLabel = new Label("Progress: " + unlockedCount + "/" + totalCount + " achievements unlocked", skin);
        statsLabel.setFontScale(1.2f);
        statsLabel.setColor(Color.CYAN);
        achievementsWindow.add(statsLabel).padBottom(20).row();
        
        // Добавляем контейнер с достижениями
        achievementsWindow.add(achievementsContainer).expand().fill().row();
        
        // Кнопки управления
        Table buttonTable = new Table(skin);
        
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                achievementsWindow.remove();
            }
        });
        
        TextButton refreshButton = new TextButton("Refresh", skin);
        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                achievementsWindow.remove();
                showAchievementsMenu();
            }
        });
        
        buttonTable.add(closeButton).width(150).height(50).padRight(10);
        buttonTable.add(refreshButton).width(150).height(50);
        
        achievementsWindow.add(buttonTable).padTop(20);
        
        // Добавляем окно на сцену
        stage.addActor(achievementsWindow);
        achievementsWindow.toFront();
    }
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    @Override
    public void resume() {
        if (backgroundMusic != null && SettingsScreen.isMusicEnabled() && !backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    @Override
    public void hide() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }


    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        stage.dispose();
        skin.dispose();
        if (clickEffect != null) {
            clickEffect.dispose();
        }
        if (clickSound != null) {
            clickSound.dispose();
        }
        if (achievementSound != null) {
            achievementSound.dispose();
        }
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
        if (bossTexture != null) {
            bossTexture.dispose();
        }
    }


}