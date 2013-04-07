import java.awt.Color;

import javax.vecmath.Vector3f;

import processing.core.PApplet;

import Objects.Box;
import Objects.PObject;
import Objects.Player;
import Utils.Input;

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
	
	private Player player;
   
    @Override
	public void setup() {
		size(800, 600, P3D);
		//noStroke();
		strokeWeight(0.1f);  // Default
		lights();
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
		Vector3f worldAabbMin = new Vector3f(-5000, -5000, -5000);
		Vector3f worldAabbMax = new Vector3f(5000, 5000, 5000);
		int maxProxies = 102;
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);

		// the default constraint solver. For parallel processing you can use a
		// different solver (see Extras/BulletMultiThreaded)
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);

		Box b = new Box(new Vector3f(), new Vector3f(150, 5, 450), 0, Color.GRAY, this);
		dynamicsWorld.addRigidBody(b.body);
		
		b = new Box(new Vector3f(200, 0, 0), new Vector3f(150, 5, 450), 0, Color.GRAY, this);
		dynamicsWorld.addRigidBody(b.body);
		
		b = new Box(new Vector3f(0, 100, 0), new Vector3f(150, 5, 150), 0, Color.GRAY, this);
		dynamicsWorld.addRigidBody(b.body);
		
		b = new Box(new Vector3f(-50, 100, 0), new Vector3f(5, 150, 150), 0, Color.GRAY, this);
		dynamicsWorld.addRigidBody(b.body);
		
		b = new Box(new Vector3f(50, 100, 0), new Vector3f(5, 150, 150), 0, Color.GRAY, this);
		dynamicsWorld.addRigidBody(b.body);
		
		b = new Box(new Vector3f(0, 150, 0), new Vector3f(20, 20, 20), 1, Color.RED, this);
		dynamicsWorld.addRigidBody(b.body);
		b = new Box(new Vector3f(150, 150, 0), new Vector3f(20, 20, 20), 1, Color.RED, this);
		dynamicsWorld.addRigidBody(b.body);
		b = new Box(new Vector3f(150, 150, 100), new Vector3f(20, 20, 20), 1, Color.RED, this);
		dynamicsWorld.addRigidBody(b.body);
		
		player = new Player(new Vector3f(0, 250, 0), this);
		dynamicsWorld.addRigidBody(player.body);
    }

	@Override
	public void draw() {
		background(0, 0, 0);
		translate(width/2f, height/2f, 0);
		
		// Print FPS every 120 frames (ideally, 2 seconds)
		if (frameCount % 200 == 0) {
			System.out.println("" + frameRate + " FPS");
		}
		
		Vector3f playerPos = player.getGraphicsPos();
		float playerRotation= player.getRotation();
		
		camera(playerPos.x + 100*cos(playerRotation), 
			   playerPos.y - 50, 
			   playerPos.z + 100*sin(playerRotation), 
			   playerPos.x, 
			   playerPos.y, 
			   playerPos.z, 
			   0, 1, 0);
		
		// Do physics simulation
		dynamicsWorld.stepSimulation(1.f / 60.f, 10);
		for (int j=dynamicsWorld.getNumCollisionObjects()-1; j>=0; j--) {
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
		Input.keyPressed(keyCode);
	}

	@Override
	public void keyReleased() {
		Input.keyReleased(keyCode);
	}
}
