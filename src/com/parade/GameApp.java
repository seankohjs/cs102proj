package com.parade;
import com.parade.controller.GameController;

public class GameApp {
    public static void main(String[] args) {
        // Always restart the game
        while(true){
            GameController.initialize();
        }
    }
}
