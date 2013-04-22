
import java.awt.Color;

import javax.vecmath.Vector2f;

import processing.core.PConstants;

public class MenuScene extends Scene {
	private PhysicsBackground background;
	
    public MenuScene(SpheresVsCubes app_) {
        super(app_);
    }

    @Override
    public void draw() {
        background.draw();
        
        super.draw();
        
        applet.fill(255);
        applet.textSize(50);
        applet.textAlign(PConstants.CENTER);
        applet.text("Spheres vs. Cubes", applet.width/2, applet.height/2 - 100);
    }

    @Override
    public void init() {
    	applet.camera();
    	background = new PhysicsBackground(BackgroundType.Menu, applet);
    	
        Button playButton = Button.createButton(new Vector2f(applet.width/2 - 50, applet.height/2 + 50), 100, 30, new ButtonCallback() {
            @Override
            public void call() {
            	GameScene gameScene = new GameScene(applet);
            	applet.changeScene(gameScene);
            	gameScene.loadLevel("levels/level1");
            }
        }, applet);
        playButton.fill = Color.GREEN;
        playButton.text = "Play";
    }
}
