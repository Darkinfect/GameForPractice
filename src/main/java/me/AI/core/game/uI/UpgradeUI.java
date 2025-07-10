package me.AI.core.game.uI;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import me.AI.core.game.entities.Player;
import me.AI.core.game.systems.UpgradeSystem;
import me.AI.core.game.utils.GameData;

public class UpgradeUI {
    private BitmapFont font;
    private BitmapFont titleFont;
    private UpgradeSystem upgradeSystem;
    private GameData gameData;
    private ShapeRenderer shapeRenderer;
    private boolean visible;

    public UpgradeUI(BitmapFont font, BitmapFont titleFont, UpgradeSystem upgradeSystem, GameData gameData) {
        this.font = font;
        this.titleFont = titleFont;
        this.upgradeSystem = upgradeSystem;
        this.gameData = gameData;
        this.shapeRenderer = new ShapeRenderer();
        this.visible = false;
    }

    public void render(SpriteBatch batch, Player player) {
        if (!visible) return;

        // Draw background
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.8f);
        shapeRenderer.rect(200, 100, 400, 400);
        shapeRenderer.setColor(0.2f, 0.2f, 0.8f, 1);
        shapeRenderer.rect(205, 105, 390, 390);
        shapeRenderer.end();
        batch.begin();

        // Title
        titleFont.draw(batch, "UPGRADES", 320, 470);

        // Money
        font.draw(batch, "Money: $" + gameData.getMoney(), 220, 440);

        // Upgrade options
        int y = 400;

        // Damage upgrade
        int damageCost = upgradeSystem.getUpgradeCost("damage", player.getDamageLevel());
        font.draw(batch, "Damage Level " + player.getDamageLevel(), 220, y);
        if (upgradeSystem.canAfford("damage", player.getDamageLevel())) {
            font.draw(batch, "Press 1 - $" + damageCost, 220, y - 25);
        } else {
            font.draw(batch, "Need $" + damageCost, 220, y - 25);
        }
        y -= 70;

        // Health upgrade
        int healthCost = upgradeSystem.getUpgradeCost("health", player.getHealthLevel());
        font.draw(batch, "Health Level " + player.getHealthLevel(), 220, y);
        if (upgradeSystem.canAfford("health", player.getHealthLevel())) {
            font.draw(batch, "Press 2 - $" + healthCost, 220, y - 25);
        } else {
            font.draw(batch, "Need $" + healthCost, 220, y - 25);
        }
        y -= 70;

        // Speed upgrade
        int speedCost = upgradeSystem.getUpgradeCost("speed", player.getSpeedLevel());
        font.draw(batch, "Speed Level " + player.getSpeedLevel(), 220, y);
        if (upgradeSystem.canAfford("speed", player.getSpeedLevel())) {
            font.draw(batch, "Press 3 - $" + speedCost, 220, y - 25);
        } else {
            font.draw(batch, "Need $" + speedCost, 220, y - 25);
        }
        y -= 70;

        // Fire rate upgrade
        int fireRateCost = upgradeSystem.getUpgradeCost("firerate", player.getFireRateLevel());
        font.draw(batch, "Fire Rate Level " + player.getFireRateLevel(), 220, y);
        if (upgradeSystem.canAfford("firerate", player.getFireRateLevel())) {
            font.draw(batch, "Press 4 - $" + fireRateCost, 220, y - 25);
        } else {
            font.draw(batch, "Need $" + fireRateCost, 220, y - 25);
        }

        // Instructions
        font.draw(batch, "Press U to close", 220, 140);
    }

    public void toggle() {
        visible = !visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
