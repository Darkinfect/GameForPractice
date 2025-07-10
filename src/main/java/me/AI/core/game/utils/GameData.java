package me.AI.core.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameData {
    private int money;
    private int totalMoney;
    private int enemiesKilled;
    private int clickCount;
    private Preferences prefs;

    public GameData() {
        prefs = Gdx.app.getPreferences("SpaceClickerSave");
        loadData();
    }

    public void addMoney(int amount) {
        money += amount;
        totalMoney += amount;
        saveData();
    }

    public void spendMoney(int amount) {
        money -= amount;
        saveData();
    }

    public void incrementEnemiesKilled() {
        enemiesKilled++;
        saveData();
    }

    public void incrementClickCount() {
        clickCount++;
        saveData();
    }

    public void saveData() {
        prefs.putInteger("money", money);
        prefs.putInteger("totalMoney", totalMoney);
        prefs.putInteger("enemiesKilled", enemiesKilled);
        prefs.putInteger("clickCount", clickCount);
        prefs.flush();
    }

    public void loadData() {
        money = prefs.getInteger("money", 0);
        totalMoney = prefs.getInteger("totalMoney", 0);
        enemiesKilled = prefs.getInteger("enemiesKilled", 0);
        clickCount = prefs.getInteger("clickCount", 0);
    }

    public void resetData() {
        money = 0;
        totalMoney = 0;
        enemiesKilled = 0;
        clickCount = 0;
        saveData();
    }

    // Getters
    public int getMoney() { return money; }
    public int getTotalMoney() { return totalMoney; }
    public int getEnemiesKilled() { return enemiesKilled; }
    public int getClickCount() { return clickCount; }
}
