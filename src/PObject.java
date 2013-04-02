import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

import processing.core.PApplet;


public abstract class PObject {
	protected PApplet applet;
	public RigidBody body;
	private Transform trans;
	
	PObject(PApplet applet_) {
		applet = applet_;
		trans = new Transform();
	}
	
	public void visit() {
		applet.pushMatrix();
		body.getMotionState().getWorldTransform(trans);
		applet.translate(trans.origin.x, -trans.origin.y, trans.origin.z);
		draw();
		applet.popMatrix();
	}
	public abstract void draw();
}
