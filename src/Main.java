import game.SceneManager;
import game.Window;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        Window window = new Window(new Dimension(854,480));
        window.initRenderSystem();
        SceneManager sceneManager = new SceneManager(window);
        sceneManager.start();
    }
}
