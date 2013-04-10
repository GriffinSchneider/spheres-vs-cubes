import processing.core.PApplet;

public class SpheresVsCubes extends PApplet {
	private static final long serialVersionUID = 1L;
    
	public static void main(String[] args) {
		PApplet.main(new String[] { "SpheresVsCubes" });
	}

    public boolean isEditorMode = false;
   
	private Scene currentScene;
	private Scene nextScene;
	
	public void changeScene(Scene scene) {
		if (nextScene == null) {
			nextScene = scene;
		}
	}
	
    @Override
	public void setup() {
		size(800, 600, P3D);
		strokeWeight(1f);  // Default
		lights();
		
		nextScene = new MenuScene(this);
	}
    
	@Override
	public void draw() {
		if (nextScene != null) {
			if (currentScene != null) {
				currentScene.cleanUp();
			}
			currentScene = nextScene;
			nextScene = null;
			currentScene.init();
		}

		if (currentScene != null) {
			currentScene.update();
			currentScene.draw();
		}
	}
	
	@Override
	public void mousePressed() {
		Input.mousePressed();
	}
	
	@Override
	public void mouseReleased() {
		Input.mouseReleased();
	}
	
	@Override
	public void keyPressed() {
		Input.keyPressed(keyCode);
	}

	@Override
	public void keyReleased() {
		Input.keyReleased(keyCode);
	}
}
