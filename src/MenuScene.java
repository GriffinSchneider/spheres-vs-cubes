
import javax.vecmath.Vector2f;

import processing.core.PConstants;

public class MenuScene extends Scene {

    public MenuScene(SpheresVsCubes app_) {
        super(app_);
    }

    @Override
    public void draw() {
        super.draw();

        applet.textSize(50);
        applet.textAlign(PConstants.CENTER);
        applet.text("Spheres vs. Cubes", applet.width/2, applet.height/2 - 100);
    }

    @Override
    public void init() {
        Button playButton = Button.createButton(new Vector2f(applet.width/2 - 50, applet.height/2), 100, 30, new ButtonCallback() {
            @Override
            public void call() {
            	applet.changeScene(new GameScene(applet));
            }
        }, applet);
        playButton.text = "Play";
    }
}