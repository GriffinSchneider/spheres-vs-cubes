import java.awt.Color;

import javax.vecmath.Vector3f;

import processing.core.PApplet;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;


public class SpheresVsCubes extends PApplet {
	private static final long serialVersionUID = 1L;
	
	private static DiscreteDynamicsWorld dynamicsWorld;
    
	public static void main(String[] args) {
		PApplet.main(new String[] { "SpheresVsCubes" });
	}
   
    @Override
	public void setup() {
		size(500, 500, P3D);
		initPhysics();
	}
    
    public void initPhysics() {
    	// collision configuration contains default setup for memory, collision
		// setup. Advanced users can create their own configuration.
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();

		// use the default collision dispatcher. For parallel processing you
		// can use a diffent dispatcher (see Extras/BulletMultiThreaded)
		CollisionDispatcher dispatcher = new CollisionDispatcher(
				collisionConfiguration);

		// the maximum size of the collision world. Make sure objects stay
		// within these boundaries
		// Don't make the world AABB size too large, it will harm simulation
		// quality and performance
		Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
		Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
		int maxProxies = 1024;
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);

		// the default constraint solver. For parallel processing you can use a
		// different solver (see Extras/BulletMultiThreaded)
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);

		dynamicsWorld.setGravity(new Vector3f(0, -10, 0));

		Box b = new Box(new Vector3f(), new Vector3f(150, 5, 150), 0, Color.WHITE, this);
		dynamicsWorld.addRigidBody(b.body);
		
		b = new Box(new Vector3f(0, 150, 0), new Vector3f(20, 20, 20), 5, Color.RED, this);
		dynamicsWorld.addRigidBody(b.body);
		
		b = new Box(new Vector3f(-50, 150, 0), new Vector3f(20, 20, 20), 5, Color.RED, this);
		dynamicsWorld.addRigidBody(b.body);
		
		b = new Box(new Vector3f(50, 150, 0), new Vector3f(20, 20, 20), 5, Color.RED, this);
		dynamicsWorld.addRigidBody(b.body);
		
		b = new Box(new Vector3f(150, 150, 0), new Vector3f(20, 20, 20), 5, Color.RED, this);
		dynamicsWorld.addRigidBody(b.body);

		/*
		{
			// create a dynamic rigidbody
			CollisionShape colShape = new SphereShape(1.f);

			// Create Dynamic Objects
			Transform startTransform = new Transform();
			startTransform.setIdentity();

			float mass = 1f;

			// rigidbody is dynamic if and only if mass is non zero,
			// otherwise static
			boolean isDynamic = (mass != 0f);

			Vector3f localInertia = new Vector3f(0, 0, 0);
			if (isDynamic) {
				colShape.calculateLocalInertia(mass, localInertia);
			}

			startTransform.origin.set(new Vector3f(2, 10, 0));

			// using motionstate is recommended, it provides
			// interpolation capabilities, and only synchronizes
			// 'active' objects
			DefaultMotionState myMotionState = new DefaultMotionState(startTransform);

			RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(
					mass, myMotionState, colShape, localInertia);
			RigidBody body = new RigidBody(rbInfo);

			dynamicsWorld.addRigidBody(body);
		}
		*/
    }

	@Override
	public void draw() {
		background(0, 0, 0);
		translate(width/2f, height/2f, 0);
		
		// Draw FPS counter
		pushMatrix();
		translate(0, 0, 100);
		fill(255, 255, 255);
		text("" + frameRate + " FPS", -(width/2f) + 60, -(height/2f) + 70);
		popMatrix();
		
		// Print FPS every 120 frames (ideally, 2 seconds)
		if (frameCount % 200 == 0) {
			System.out.println("" + frameRate + " FPS");
		}
		
		// Do physics simulation
		dynamicsWorld.stepSimulation(1.f / 60.f, 10);
		// print positions of all objects
		for (int j=dynamicsWorld.getNumCollisionObjects()-1; j>=0; j--)
		{
			CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(j);
			RigidBody body = RigidBody.upcast(obj);
			if (body != null && body.getMotionState() != null) {
				PObject pobj;
				if ((pobj = (PObject) body.getUserPointer()) != null) {
					pobj.visit();
				}
			}
		}
	}

	@Override
	public void keyPressed() {

	}

}
