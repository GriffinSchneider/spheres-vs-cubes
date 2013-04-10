import java.awt.Color;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;


public class PhysicsBackground extends Node {
	public static final float WORLD_GRAVITY = 500f / PObject.GRAPHICS_UNITS_PER_PHYSICS_UNITS;
	private static final int CREATE_DELAY = 1;
	
	private int count;
	private DiscreteDynamicsWorld dynamicsWorld;
	private Sphere sphere;
	
	public PhysicsBackground(SpheresVsCubes applet_) {
		super(applet_);
		count = 0;
		initPhysics();
		sphere = new Sphere(new Vector3f(applet.width / 2, -applet.height / 2, 0), 
				Player.PLAYER_INITIAL_RADIUS, 20, Color.GREEN, dynamicsWorld, applet);
		sphere.body.setGravity(new Vector3f());
		sphere.body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
	}

	private void initPhysics() {
		// collision configuration contains default setup for memory, collision
		// setup. Advanced users can create their own configuration.
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();

		// use the default collision dispatcher. For parallel processing you
		// can use a different dispatcher (see Extras/BulletMultiThreaded)
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

		// the maximum size of the collision world. Make sure objects stay
		// within these boundaries
		// Don't make the world AABB size too large, it will harm simulation
		// quality and performance
		Vector3f worldAabbMin = new Vector3f(-5000, -5000, -5000);
		Vector3f worldAabbMax = new Vector3f(5000, 5000, 5000);
		int maxProxies = 100;
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);

		// the default constraint solver. For parallel processing you can use a
		// different solver (see Extras/BulletMultiThreaded)
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0, -WORLD_GRAVITY, 0));
    }

	
	
	@Override
	public void draw() {
		applet.background(0, 0, 0);
		dynamicsWorld.stepSimulation(1.f / 60.f, 10);

		if (count++ > CREATE_DELAY) {
			count = 0;
			new Enemy(sphere, new Vector3f(applet.random(applet.width), 40, 0), dynamicsWorld, applet);
		}
		
		sphere.setGraphicsPos(new Vector3f(applet.mouseX, applet.mouseY, 0));
		
		for (int j=dynamicsWorld.getNumCollisionObjects()-1; j>=0; j--) {
			CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(j);
			
			RigidBody body = RigidBody.upcast(obj);
			if (body != null && body.getMotionState() != null) {
				PObject pobj;
				if ((pobj = (PObject) body.getUserPointer()) != null) {
					pobj.visit();
					if (pobj.getGraphicsPos().y < -applet.height) {
						pobj.remove();
					}
				}
			}
		}
	}

	@Override
	public void update() {
	}
}
