
import javax.vecmath.Vector2f;

import processing.core.PConstants;

public class GameOverScene extends Scene {
	private PhysicsBackground background;
	
    public GameOverScene(SpheresVsCubes applet_) {
        super(applet_);
    }

    @Override
    public void draw() {
    	background.draw();
    	
        super.draw();

        applet.textSize(50);
        applet.textAlign(PConstants.CENTER);
        applet.text("You Won!", applet.width/2, applet.height/2 - 100);
        applet.textSize(30);
        //applet.text("Restarts used: " + s.resetsUsed + "\nRadius lost: " + s.radiusLost, applet.width/2, applet.height/2);
    }

    @Override
    public void init() {
    	applet.camera();
    	background = new PhysicsBackground(BackgroundType.Victory, applet);
    	
        Button menuButton = Button.createButton(new Vector2f(applet.width/2-100, applet.height/2+100), 200, 30, new ButtonCallback() {
            @Override
            public void call() {
                // Return to the main menu
                applet.changeScene(new MenuScene(applet));
            }
        }, applet);
        menuButton.text = "Return to Main Menu";
    }
}
