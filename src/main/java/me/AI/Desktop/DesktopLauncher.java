package me.AI.Desktop;

import me.AI.core.game.SpaceClickerGame;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import me.darkinfect.ClickerGame;

public class DesktopLauncher {
    public static void main (String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(800,600);
        config.setResizable(false);
        new Lwjgl3Application(new SpaceClickerGame(), config);
    }
}
