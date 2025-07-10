package me.AI.core.game.systems;

import me.AI.core.game.entities.Player;
import me.AI.core.game.utils.GameData;

public class UpgradeSystem {
    private GameData gameData;

    public UpgradeSystem(GameData gameData) {
        this.gameData = gameData;
    }

    public int getUpgradeCost(String upgradeType, int currentLevel) {
        int baseCost = getBaseCost(upgradeType);
        return (int)(baseCost * Math.pow(1.5, currentLevel - 1));
    }

    private int getBaseCost(String upgradeType) {
        switch(upgradeType) {
            case "damage": return 100;
            case "health": return 150;
            case "speed": return 200;
            case "firerate": return 300;
            default: return 100;
        }
    }

    public boolean canAfford(String upgradeType, int currentLevel) {
        int cost = getUpgradeCost(upgradeType, currentLevel);
        return gameData.getMoney() >= cost;
    }

    public boolean purchaseUpgrade(String upgradeType, Player player) {
        int currentLevel = getCurrentLevel(upgradeType, player);
        int cost = getUpgradeCost(upgradeType, currentLevel);

        if (gameData.getMoney() >= cost) {
            gameData.spendMoney(cost);
            applyUpgrade(upgradeType, player);
            return true;
        }
        return false;
    }

    private int getCurrentLevel(String upgradeType, Player player) {
        switch(upgradeType) {
            case "damage": return player.getDamageLevel();
            case "health": return player.getHealthLevel();
            case "speed": return player.getSpeedLevel();
            case "firerate": return player.getFireRateLevel();
            default: return 1;
        }
    }

    private void applyUpgrade(String upgradeType, Player player) {
        switch(upgradeType) {
            case "damage":
                player.upgradeDamage();
                break;
            case "health":
                player.upgradeHealth();
                break;
            case "speed":
                player.upgradeSpeed();
                break;
            case "firerate":
                player.upgradeFireRate();
                break;
        }
    }
}
