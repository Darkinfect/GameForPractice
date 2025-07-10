package me.darkinfect.scenes;

public class Achievement {
    public enum AchievementType {
        CLICKS, COINS, BOSSES, UPGRADES, TIME, SPECIAL
    }
    
    private final String id;
    private final String name;
    private final String description;
    private final AchievementType type;
    private final int targetValue;
    private final int rewardCoins;
    private boolean unlocked;
    private int currentProgress;
    
    public Achievement(String id, String name, String description, AchievementType type, int targetValue, int rewardCoins) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.targetValue = targetValue;
        this.rewardCoins = rewardCoins;
        this.unlocked = false;
        this.currentProgress = 0;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public AchievementType getType() {
        return type;
    }
    
    public int getTargetValue() {
        return targetValue;
    }
    
    public int getRewardCoins() {
        return rewardCoins;
    }
    
    public boolean isUnlocked() {
        return unlocked;
    }
    
    public int getCurrentProgress() {
        return currentProgress;
    }
    
    public float getProgressPercentage() {
        return (float) currentProgress / targetValue;
    }
    
    public void updateProgress(int value) {
        if (!unlocked) {
            this.currentProgress = Math.min(value, targetValue);
            if (currentProgress >= targetValue) {
                unlock();
            }
        }
    }
    
    public void unlock() {
        if (!unlocked) {
            this.unlocked = true;
            this.currentProgress = targetValue;
        }
    }
    
    public String getProgressText() {
        if (unlocked) {
            return "✓ Completed";
        } else {
            return currentProgress + "/" + targetValue;
        }
    }
} 