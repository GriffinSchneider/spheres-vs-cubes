

public abstract class Scene extends Node {
    public Scene(SpheresVsCubes applet_) {
        super(applet_);
    }
    
    public abstract void init();
    
    @Override
    public void draw() {
        Button.displayButtons();
    }
    
    @Override
    public void update() {
        Button.updateButtons();
    }
    
    public void cleanUp() {
        Button.removeButtons();
    }
    
    public void keyPressed(int keyCode) {}
    public void mouseMoved(int mouseX, int mouseY) {}
}
