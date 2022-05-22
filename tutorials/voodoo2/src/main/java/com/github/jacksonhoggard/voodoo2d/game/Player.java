package main.java.com.github.jacksonhoggard.voodoo2d.game;

import main.java.com.github.jacksonhoggard.voodoo2d.engine.MouseInput;
import main.java.com.github.jacksonhoggard.voodoo2d.engine.Window;
import main.java.com.github.jacksonhoggard.voodoo2d.engine.animation.Animation;
import main.java.com.github.jacksonhoggard.voodoo2d.engine.gameObject.GameObject;
import main.java.com.github.jacksonhoggard.voodoo2d.engine.graphic.Mesh;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class Player extends GameObject {

    private Animation[] animations;

    public Player() {
        super();
        animations = new Animation[0];
        setScale(0.2f);
    }

    public void init() {
        this.setMesh(Mesh.loadMesh("textures/player.png", 64));
        Animation runDown = new Animation(this, 0, 3, 6);
        Animation runLeft = new Animation(this, 4, 7, 6);
        Animation runRight = new Animation(this, 8, 11, 6);
        Animation runUp = new Animation(this, 12, 15, 6);
        animations = new Animation[]{runDown, runLeft, runRight, runUp};
    }

    public void input(Window window, MouseInput mouseInput) {
        if(window.isKeyPressed(GLFW_KEY_S))
            animations[0].play();
        else
            animations[0].stop();
        if(window.isKeyPressed(GLFW_KEY_A))
            animations[1].play();
        else
            animations[1].stop();
        if(window.isKeyPressed(GLFW_KEY_D))
            animations[2].play();
        else
            animations[2].stop();
        if(window.isKeyPressed(GLFW_KEY_W))
            animations[3].play();
        else
            animations[3].stop();
    }


}
