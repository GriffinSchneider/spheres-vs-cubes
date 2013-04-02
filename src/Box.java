import java.awt.Color;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import processing.core.PApplet;


public class Box extends PObject {
	private Vector3f pos;
	private Vector3f dim;
	private float mass;
	public Color color;
	
	Box(Vector3f pos_, Vector3f dim_, float mass_, Color color_, PApplet applet_) {
		super(applet_);
		pos = pos_;
		dim = dim_;
		mass = mass_;
		color = color_;
		initPhysics();
	}
	
	public void initPhysics() {
		// create a few basic rigid bodies
		CollisionShape groundShape = new BoxShape(new Vector3f(dim.x / 2f, dim.y / 2f, dim.z / 2f));
		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(pos);

		// rigidbody is dynamic if and only if mass is non zero,
		// otherwise static
		boolean isDynamic = (mass != 0f);

		Vector3f localInertia = new Vector3f(0, 0, 0);
		if (isDynamic) {
			groundShape.calculateLocalInertia(mass, localInertia);
		}

		// using motionstate is recommended, it provides interpolation
		// capabilities, and only synchronizes 'active' objects
		DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, groundShape, localInertia);
		body = new RigidBody(rbInfo);
		body.setUserPointer(this);
	}

	@Override
	public void draw() {
		//applet.translate(dim.x / 2, dim.y / 1.5f, dim.z / 2);
		applet.fill(color.getRGB());
		applet.box(dim.x, dim.y, dim.z);
	}
}
