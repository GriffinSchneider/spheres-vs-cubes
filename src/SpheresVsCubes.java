import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import processing.core.PApplet;

import Objects.Box;
import Objects.PObject;
import Objects.Sphere;

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
	
	private Sphere player;
   
    @Override
	public void setup() {
		size(500, 500, P3D);
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
		Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
		Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
		int maxProxies = 1024;
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);

		// the default constraint solver. For parallel processing you can use a
		// different solver (see Extras/BulletMultiThreaded)
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);

		dynamicsWorld.setGravity(new Vector3f(0, -10, 0));

		Box b = new Box(new Vector3f(), new Vector3f(150, 5, 450), 0, Color.GRAY, this);
		dynamicsWorld.addRigidBody(b.body);
		
		b = new Box(new Vector3f(200, 0, 0), new Vector3f(150, 5, 450), 0, Color.GRAY, this);
		dynamicsWorld.addRigidBody(b.body);
		
		b = new Box(new Vector3f(0, 150, 0), new Vector3f(20, 20, 20), 5, Color.RED, this);
		dynamicsWorld.addRigidBody(b.body);
		
		b = new Box(new Vector3f(-50, 150, 0), new Vector3f(20, 20, 20), 5, Color.RED, this);
		dynamicsWorld.addRigidBody(b.body);
		
		b = new Box(new Vector3f(50, 150, 0), new Vector3f(20, 20, 20), 5, Color.RED, this);
		dynamicsWorld.addRigidBody(b.body);
		
		b = new Box(new Vector3f(150, 150, 0), new Vector3f(20, 20, 20), 5, Color.RED, this);
		dynamicsWorld.addRigidBody(b.body);
		
		player = new Sphere(new Vector3f(0, 250, 0), 20, 1, Color.GREEN, this);
		dynamicsWorld.addRigidBody(player.body);
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
		
		beginCamera();
		
		Vector3f pos = player.getPos();
		// Rotate the player
		if (Input.checkKey(KeyEvent.VK_A) || Input.checkKey(LEFT)) {
			rotation-=3;
		}
		else if (Input.checkKey(KeyEvent.VK_D) || Input.checkKey(RIGHT)) {
			rotation+=3;
		}
		float rads = radians(rotation);
		float speed = 25;
		// Move the player
		Vector3f velocity = player.body.getLinearVelocity(new Vector3f());
		if (Input.checkKey(KeyEvent.VK_W) || Input.checkKey(UP)) {
			velocity.x = -speed * cos(rads);
			velocity.z = -speed * sin(rads);
		}
		else if (Input.checkKey(KeyEvent.VK_S) || Input.checkKey(DOWN)) {
			velocity.x = speed * cos(rads);
			velocity.z = speed * sin(rads);
		}
		else {
			velocity.x = 0;
			velocity.z = 0;
		}
		player.body.setLinearVelocity(velocity);
		// Make the player jump
		if (Input.checkKey(KeyEvent.VK_SPACE)) {
			player.body.applyCentralImpulse(new Vector3f(0, 1, 0));
		}
		
		
		camera(pos.x + 100 * cos(rads), pos.y - 50, pos.z + 100 * sin(rads), pos.x, pos.y, pos.z, 0, 1, 0);
		
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
		endCamera();
	}

	private float rotation = 0;
	@Override
	public void keyPressed() {
		Input.keyPressed(keyCode);
	}

	@Override
	public void keyReleased() {
		Input.keyReleased(keyCode);
	}
}
