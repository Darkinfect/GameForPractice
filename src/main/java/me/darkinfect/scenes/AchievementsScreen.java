package me.darkinfect.scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;

public class AchievementsScreen implements Screen, Disposable {
    private Game game;
    private Stage stage;
    private SpriteBatch batch;
    private Skin skin;
    private ArrayList<Achievement> achievements;
    private ScrollPane scrollPane;
    private Table mainTable;
    private Table achievementsContainer;
    
    public AchievementsScreen(Game game, ArrayList<Achievement> achievements) {
        this.game = game;
        this.achievements = achievements;
        this.stage = new Stage();
        this.batch = new SpriteBatch();
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        
        initUI();
        Gdx.input.setInputProcessor(stage);
    }
    
    private void initUI() {
        // Создаем главную таблицу
        mainTable = new Table(skin);
        mainTable.setFillParent(true);
        mainTable.setBackground(new TextureRegionDrawable(new TextureRegion(createWhitePixel(Color.DARK_GRAY))));
        mainTable.pad(20);
        
        // Заголовок
        Label titleLabel = new Label("ACHIEVEMENTS", skin);
        titleLabel.setFontScale(3.0f);
        titleLabel.setColor(Color.CYAN);
        mainTable.add(titleLabel).padBottom(30).row();
        
        // Статистика
        int unlockedCount = 0;
        int totalCount = achievements.size();
        for (Achievement achievement : achievements) {
            if (achievement.isUnlocked()) {
                unlockedCount++;
            }
        }
        
        Label statsLabel = new Label("Progress: " + unlockedCount + "/" + totalCount + " achievements unlocked", skin);
        statsLabel.setFontScale(1.5f);
        statsLabel.setColor(Color.CYAN);
        mainTable.add(statsLabel).padBottom(20).row();
        
        // Создаем контейнер для достижений
        achievementsContainer = new Table(skin);
        achievementsContainer.align(Align.topLeft);
        
        // Добавляем все достижения
        for (Achievement achievement : achievements) {
            addAchievementCard(achievement);
        }
        
        // Создаем скроллируемую область
        scrollPane = new ScrollPane(achievementsContainer, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollBarPositions(false, true);
        
        mainTable.add(scrollPane).expand().fill().padBottom(20).row();
        
        // Кнопки управления
        Table buttonTable = new Table(skin);
        
        TextButton backButton = new TextButton("Back to Game", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(MainScene.getIntsance(game));
            }
        });
        
        TextButton refreshButton = new TextButton("Refresh", skin);
        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                refreshAchievements();
            }
        });
        
        buttonTable.add(backButton).width(200).height(60).padRight(20);
        buttonTable.add(refreshButton).width(200).height(60);
        
        mainTable.add(buttonTable);
        
        stage.addActor(mainTable);
    }
    
    private void addAchievementCard(Achievement achievement) {
        // Создаем карточку достижения
        Table achievementCard = new Table(skin);
        
        // Цвет фона карточки
        Color cardColor = achievement.isUnlocked() ? 
            new Color(0.2f, 0.8f, 0.2f, 0.3f) : 
            new Color(0.3f, 0.3f, 0.3f, 0.3f);
        achievementCard.setBackground(new TextureRegionDrawable(new TextureRegion(createWhitePixel(cardColor))));
        achievementCard.pad(20);
        achievementCard.align(Align.topLeft);
        
        // Иконка статуса
        String statusIcon = achievement.isUnlocked() ? "✓" : "○";
        Color statusColor = achievement.isUnlocked() ? Color.GREEN : Color.GRAY;
        
        Label statusLabel = new Label(statusIcon, skin);
        statusLabel.setFontScale(2.0f);
        statusLabel.setColor(statusColor);
        
        // Название достижения
        Label nameLabel = new Label(achievement.getName(), skin);
        nameLabel.setFontScale(1.5f);
        nameLabel.setColor(achievement.isUnlocked() ? Color.GOLD : Color.CYAN);
        
        // Описание
        Label descLabel = new Label(achievement.getDescription(), skin);
        descLabel.setFontScale(1.2f);
        descLabel.setColor(Color.WHITE);
        
        // Прогресс
        Label progressLabel = new Label(achievement.getProgressText(), skin);
        progressLabel.setFontScale(1.3f);
        progressLabel.setColor(achievement.isUnlocked() ? Color.GREEN : Color.YELLOW);
        
        // Награда
        Label rewardLabel = new Label("Reward: " + achievement.getRewardCoins() + " coins", skin);
        rewardLabel.setFontScale(1.2f);
        rewardLabel.setColor(Color.CYAN);
        
        // Тип достижения
        Label typeLabel = new Label("Type: " + achievement.getType().toString(), skin);
        typeLabel.setFontScale(1.0f);
        typeLabel.setColor(Color.LIGHT_GRAY);
        
        // Добавляем элементы в карточку
        Table leftColumn = new Table(skin);
        leftColumn.add(statusLabel).padRight(15);
        
        Table rightColumn = new Table(skin);
        rightColumn.align(Align.topLeft);
        rightColumn.add(nameLabel).padBottom(8).row();
        rightColumn.add(descLabel).padBottom(8).row();
        rightColumn.add(progressLabel).padBottom(8).row();
        rightColumn.add(rewardLabel).padBottom(5).row();
        rightColumn.add(typeLabel);
        
        achievementCard.add(leftColumn);
        achievementCard.add(rightColumn).expand().fill();
        
        // Добавляем карточку в контейнер
        achievementsContainer.add(achievementCard).width(800).height(150).padBottom(15).row();
    }
    
    private void refreshAchievements() {
        // Обновляем статистику
        int unlockedCount = 0;
        int totalCount = achievements.size();
        for (Achievement achievement : achievements) {
            if (achievement.isUnlocked()) {
                unlockedCount++;
            }
        }
        
        // Пересоздаем контейнер
        achievementsContainer.clear();
        for (Achievement achievement : achievements) {
            addAchievementCard(achievement);
        }
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
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
    
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        skin.dispose();
    }
} 