package Objects;
import java.awt.Color;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import processing.core.PApplet;


public abstract class PObject {
	protected PApplet applet;
	public RigidBody body;
	private Transform trans;
	protected Vector3f pos;
	protected float mass;
	public Color color;
	
	PObject(Vector3f pos_, float mass_, Color color_, PApplet applet_) {
		applet = applet_;
		trans = new Transform();
		pos = pos_;
		mass = mass_;
		color = color_;
	}
	
	public Vector3f getPos() {
		Vector3f tmp = new Vector3f(body.getMotionState().getWorldTransform(trans).origin);
		tmp.y = -tmp.y;
		return tmp;
	}
	
	protected void addShape(CollisionShape shape) {
		trans.setIdentity();
		trans.origin.set(pos);

		// rigidbody is dynamic if and only if mass is non zero,
		// otherwise static
		boolean isDynamic = (mass != 0f);

		Vector3f localInertia = new Vector3f(0, 0, 0);
		if (isDynamic) {
			shape.calculateLocalInertia(mass, localInertia);
		}

		// using motionstate is recommended, it provides interpolation
		// capabilities, and only synchronizes 'active' objects
		DefaultMotionState myMotionState = new DefaultMotionState(trans);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape, localInertia);
		body = new RigidBody(rbInfo);
		body.setUserPointer(this);
	}
	
	public void visit() {
		update();
		
		applet.pushMatrix();
		body.getMotionState().getWorldTransform(trans);
		applet.translate(trans.origin.x, -trans.origin.y, trans.origin.z);
		Quat4f r = trans.getRotation(new Quat4f());
		applet.rotate(r.w, r.x, r.y, r.z);
		
		draw();
		applet.popMatrix();
	}
	
	public abstract void draw();
	public abstract void update();
}
