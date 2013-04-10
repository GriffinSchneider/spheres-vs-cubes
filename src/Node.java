
public abstract class Node {
	protected SpheresVsCubes applet;
	
	public Node(SpheresVsCubes applet_) {
		applet = applet_;
	}
	
	public abstract void draw();
	public abstract void update();
}
