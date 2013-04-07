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
	public static float GRAPHICS_UNITS_PER_PHYSICS_UNITS = 50.0f;
	
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
	
	public Vector3f getPhysicsPos() {
		Vector3f tmp = new Vector3f(body.getMotionState().getWorldTransform(trans).origin);
		tmp.y = -tmp.y;
		return tmp;
	}
	
	public Vector3f getGraphicsPos() {
		Vector3f tmp = this.getPhysicsPos();
		tmp.scale(GRAPHICS_UNITS_PER_PHYSICS_UNITS);
		return tmp;
	}
	
	protected void addShape(CollisionShape shape) {
		trans.setIdentity();
		Vector3f physicsPos = new Vector3f(pos);
		physicsPos.scale(1.0f / GRAPHICS_UNITS_PER_PHYSICS_UNITS);
		trans.origin.set(physicsPos);

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
	
	// From http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToAngle/
	public void rotateFromQuaternion(Quat4f q1) {
		float x, y, z, angle;
		if (q1.w > 1) q1.normalize(); // if w>1 acos and sqrt will produce errors, this cant happen if quaternion is normalised
		angle = (float) (2 * Math.acos(q1.w));
		float s = (float) Math.sqrt(1-q1.w*q1.w); // assuming quaternion normalised then w is less than 1, so term always positive.
		if (s < 0.001) { // test to avoid divide by zero, s is always positive due to sqrt
			// if s close to zero then direction of axis not important
			x = q1.x; // if it is important that axis is normalised then replace with x=1; y=z=0;
			y = q1.y;
			z = q1.z;
		} else {
			x = q1.x / s; // normalise axis
			y = q1.y / s;
			z = q1.z / s;
		}
		applet.rotate(-angle, x, y, z);
	}
	
	public void visit() {
		body.activate();
		update();
		
		body.getMotionState().getWorldTransform(trans);
		
		applet.pushMatrix();
		applet.translate(
				trans.origin.x * GRAPHICS_UNITS_PER_PHYSICS_UNITS, 
				-trans.origin.y * GRAPHICS_UNITS_PER_PHYSICS_UNITS,
				trans.origin.z * GRAPHICS_UNITS_PER_PHYSICS_UNITS);		
		rotateFromQuaternion(trans.getRotation(new Quat4f()));
		draw();
		applet.popMatrix();
	}
	
	public abstract void draw();
	public abstract void update();
}
