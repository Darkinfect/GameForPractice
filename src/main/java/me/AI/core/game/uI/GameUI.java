package me.AI.core.game.uI;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.AI.core.game.entities.Player;
import me.AI.core.game.systems.EnemySpawner;
import me.AI.core.game.utils.GameData;

public class GameUI {
    private BitmapFont font;
    private BitmapFont titleFont;
    private GameData gameData;

    public GameUI(BitmapFont font, BitmapFont titleFont, GameData gameData) {
        this.font = font;
        this.titleFont = titleFont;
        this.gameData = gameData;
    }

    public void render(SpriteBatch batch, Player player, EnemySpawner spawner) {
        // Title
        titleFont.draw(batch, "SPACE CLICKER", 20, 580);

        // Player stats
        font.draw(batch, "Money: $" + gameData.getMoney(), 20, 540);
        font.draw(batch, "Health: " + (int)player.getHealth() + "/" + (int)player.getMaxHealth(), 20, 510);
        font.draw(batch, "Damage: " + (int)player.getDamage(), 20, 480);
        font.draw(batch, "Speed: " + (int)player.getSpeed(), 20, 450);

        // Game stats
        font.draw(batch, "Wave: " + spawner.getWave(), 20, 400);
        font.draw(batch, "Enemies Killed: " + gameData.getEnemiesKilled(), 20, 370);
        font.draw(batch, "Total Clicks: " + gameData.getClickCount(), 20, 340);

        // Instructions
        font.draw(batch, "Click to shoot!", 20, 280);
        font.draw(batch, "Press U for upgrades", 20, 250);
        font.draw(batch, "Press R to reset save", 20, 220);
    }
}
