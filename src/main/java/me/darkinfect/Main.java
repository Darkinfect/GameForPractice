package me.darkinfect;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Main {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowIcon("gameicon.png");
        config.setTitle("My Game");
        config.setWindowedMode(1200, 800); // Увеличиваем размер окна
        new Lwjgl3Application(new ClickerGame(), config);
    }
}
