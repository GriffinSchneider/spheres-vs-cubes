
import java.awt.Color;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;


public abstract class PObject extends Node {
	public static float GRAPHICS_UNITS_PER_PHYSICS_UNITS = 50.0f;
	
	protected DiscreteDynamicsWorld world;
	public RigidBody body;
	private Transform trans;
	public Color color;
	
	PObject(Color color_, DiscreteDynamicsWorld world_, SpheresVsCubes applet_) {
		super(applet_);
		world = world_;
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
	
	protected void addShape(Vector3f pos, float mass, CollisionShape shape) {
		this.addShape(pos, mass, shape, 0, 0);
	}
	
	protected void addShape(Vector3f pos, float mass, CollisionShape shape, float horizontalRotation, float verticalRotation) {
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

    	Quat4f q = new Quat4f();
    	Transform t = new Transform();
    	
    	
    	QuaternionUtil.setRotation(q, new Vector3f(0, 1, 0), horizontalRotation);
    	t.setRotation(q);
    	trans.mul(t);
    	
   		QuaternionUtil.setRotation(q, new Vector3f(0, 0, -1), verticalRotation);    		
    	
    	t.setRotation(q);
    	trans.mul(t);
    	
		// using motionstate is recommended, it provides interpolation
		// capabilities, and only synchronizes 'active' objects
		DefaultMotionState myMotionState = new DefaultMotionState(trans);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape, localInertia);
		body = new RigidBody(rbInfo);
		body.setUserPointer(this);
		world.addRigidBody(body);
	}

	public void visit() {
		body.activate();
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
	
	public void remove() {
		if (this.body.isInWorld()) {
			world.removeRigidBody(this.body);
		}
	}
}
