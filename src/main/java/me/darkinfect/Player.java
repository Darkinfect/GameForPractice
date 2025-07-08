package me.darkinfect;

import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 position;
    private boolean shoot = false;
    public Player(int x, int y){
        position.set(x,y);
    }
}
