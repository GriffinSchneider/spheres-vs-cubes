
import java.awt.Color;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;


public abstract class PObject {
	public static float GRAPHICS_UNITS_PER_PHYSICS_UNITS = 50.0f;
	
	protected SpheresVsCubes applet;
	public RigidBody body;
	private Transform trans;
	public Color color;
	
	PObject(Color color_, SpheresVsCubes applet_) {
		applet = applet_;
		trans = new Transform();
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
		tmp.y = -tmp.y;
		return tmp;
	}
	
	public abstract void onCollision(PObject object);
	
	protected void addShape(Vector3f pos, float mass,  CollisionShape shape) {
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
		applet.dynamicsWorld.addRigidBody(body);
	}

	public void visit() {
		update();
		body.getMotionState().getWorldTransform(trans);
		
		applet.pushMatrix();
		
		Vector3f graphicsPos = this.getGraphicsPos();
		applet.translate(graphicsPos.x, -graphicsPos.y, graphicsPos.z);
		
		Quat4f q = trans.getRotation(new Quat4f());
		applet.rotate(-QuaternionUtil.getAngle(q), q.x, -q.y, q.z);
		
		draw();
		
		applet.popMatrix();
	}
	
	public abstract void draw();
	public abstract void update();
	
	public void remove() {
		if (this.body.isInWorld()) {
			this.applet.dynamicsWorld.removeRigidBody(this.body);
		}
	}
}
