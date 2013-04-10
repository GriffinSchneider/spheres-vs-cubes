

public abstract class Scene extends Node {
    public Scene(SpheresVsCubes applet_) {
        super(applet_);
    }
    
    public abstract void init();
    
    public void draw() {
        Button.displayButtons();
    }
    
    public void update() {
        Button.updateButtons();
    }
    
    public void cleanUp() {
        Button.removeButtons();
    }
}
