package main.java.com.github.jacksonhoggard.voodoo2d.game;

import main.java.com.github.jacksonhoggard.voodoo2d.engine.GameEngine;
import main.java.com.github.jacksonhoggard.voodoo2d.engine.IGameLogic;

public class Main {
 
    public static void main(String[] args) {
        try {
            boolean vSync = false, antiAliasing = true;
            IGameLogic gameLogic = new Game();
            GameEngine gameEng = new GameEngine("ป๙วร", vSync, antiAliasing, gameLogic);
            gameEng.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}