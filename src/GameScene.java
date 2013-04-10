import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.vecmath.Vector3f;

import processing.core.PApplet;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;


public class GameScene extends Scene {

	public static final float WORLD_GRAVITY = 500f / PObject.GRAPHICS_UNITS_PER_PHYSICS_UNITS;
	
	public  float cameraDistance = 100f;
	private Player player;
	private int lastMouseX = -1;
	private int lastMouseY = -1;
	
	private DiscreteDynamicsWorld dynamicsWorld;
	
	public GameScene(SpheresVsCubes applet_) {
		super(applet_);
	}

	@Override
	public void init() {
		applet.strokeWeight(1f);  // Default
		initPhysics(); 
		applet.noCursor();
		applet.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent mwe) {
				mouseWheel(mwe.getWheelRotation());
			}
		});
	}

	public void initPhysics() {
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
		Vector3f worldAabbMin = new Vector3f(-50000, -50000, -50000);
		Vector3f worldAabbMax = new Vector3f(50000, 50000, 50000);
		int maxProxies = 150;
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);

		// the default constraint solver. For parallel processing you can use a
		// different solver (see Extras/BulletMultiThreaded)
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0, -WORLD_GRAVITY, 0));

		player = new Player(new Vector3f(0, 250, 0), dynamicsWorld, applet);
		
		new Box(new Vector3f(), new Vector3f(150, 10, 450), 0, Color.GRAY, dynamicsWorld, applet);
		
		new Box(new Vector3f(200, 0, 0), new Vector3f(150, 10, 450), 0, Color.GRAY, dynamicsWorld, applet);
		
		new Box(new Vector3f(0, 100, 0), new Vector3f(150, 10, 150), 0, Color.GRAY, dynamicsWorld, applet);
		
		new Box(new Vector3f(-50, 100, 0), new Vector3f(10, 150, 150), 0, Color.GRAY, dynamicsWorld, applet);
		
		new Box(new Vector3f(50, 100, 0), new Vector3f(10, 150, 150), 0, Color.GRAY, dynamicsWorld, applet);
		
		new Enemy(player, new Vector3f(0, 150, 0), dynamicsWorld, applet);
	
		new Enemy(player, new Vector3f(150, 150, 0), dynamicsWorld, applet);

		new Enemy(player, new Vector3f(150, 150, 100), dynamicsWorld, applet);
		
		new Box(new Vector3f(0, -100, 0), new Vector3f(5000, 100, 5000), 0, Color.GRAY, dynamicsWorld, applet);
    }
	
	@Override
	public void draw() {
		super.draw();
		
		// Do physics simulation
		if (!applet.isEditorMode) {
			dynamicsWorld.stepSimulation(1.f / 60.f, 15);
			checkCollisions();
		}
		
		if (Input.checkKey(KeyEvent.VK_EQUALS)) {
			cameraDistance -= 5;
		} else if (Input.checkKey(KeyEvent.VK_MINUS)) {
			cameraDistance += 5;
		}
		if (cameraDistance < 0) cameraDistance = 0;
		
		applet.background(0, 0, 0);
		applet.translate(applet.width / 2f, applet.height / 2f, 0);
		
		// Print FPS every 120 frames (ideally, 2 seconds)
		if (applet.frameCount % 200 == 0) {
			System.out.println("" + applet.frameRate + " FPS");
		}
		
		Vector3f playerPos = player.getGraphicsPos();
		float playerHorizontalRotation = player.getHorizontalRotation();
		float playerVerticalRotation = player.getVerticalRotation();
		
//		applet.perspective(PApplet.radians(60), applet.width / applet.height, 0.01f, 5000);
//		applet.ortho(0, applet.width, 0, applet.height, -1000, 1000); // This looks really cool
		
		// Convert our spherical coordinates (vertical + horizontal rotation) to Cartesian coordinates
		// to find the camera eye position
		applet.camera(playerPos.x + cameraDistance * PApplet.sin(playerVerticalRotation) * PApplet.cos(playerHorizontalRotation),
			   -(playerPos.y + cameraDistance * PApplet.cos(playerVerticalRotation)),
			   playerPos.z + cameraDistance * PApplet.sin(playerVerticalRotation) * PApplet.sin(playerHorizontalRotation), 
			   playerPos.x, 
			   -playerPos.y, 
			   playerPos.z, 
			   0, 1, 0);

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
	
	public void checkCollisions() {
		Vector3f ptA = new Vector3f();
		Vector3f ptB = new Vector3f();
		int numManifolds = dynamicsWorld.getDispatcher().getNumManifolds();
		for (int i = 0; i < numManifolds; i++) {
			PersistentManifold contactManifold = dynamicsWorld.getDispatcher().getManifoldByIndexInternal(i);
			if (contactManifold != null) {
				CollisionObject obA = (CollisionObject) contactManifold.getBody0();
				CollisionObject obB = (CollisionObject) contactManifold.getBody1();
			
				int numContacts = contactManifold.getNumContacts();
				for (int j=0;j<numContacts;j++)
				{
					ManifoldPoint pt = contactManifold.getContactPoint(j);
					if (pt.getDistance()<0.f)
					{
						pt.getPositionWorldOnA(ptA);
						pt.getPositionWorldOnB(ptB);
						// Vector3f normalOnB = pt.normalWorldOnB;
						
						RigidBody bodyA = RigidBody.upcast(obA);
						RigidBody bodyB = RigidBody.upcast(obB);
						
						PObject pobA = (PObject) bodyA.getUserPointer();
						PObject pobB = (PObject) bodyB.getUserPointer();
						
						pobA.onCollision(pobB);
						pobB.onCollision(pobA);
					}
				}
			}
		}
	}
	
	@Override
	public void cleanUp() {
		super.cleanUp();
		player = null;
		dynamicsWorld.destroy();
		dynamicsWorld = null;
		applet.cursor();
	}
	
	@Override
	public void keyPressed(int keyCode) {
		if (keyCode == KeyEvent.VK_E) {
			this.player.toggleEditorMode();
		} else if (keyCode == KeyEvent.VK_R) {
			this.player.placeRectangle();
		}
	}
	
	void mouseWheel(int delta) {
		this.cameraDistance += delta;
	}
	
	@Override
	public void mouseMoved(int mouseX, int mouseY) {
		// Ignore the first mouse moved since we don't know the previous position
		if (lastMouseX == -1 && lastMouseY == -1) {
			lastMouseX = mouseX;
			lastMouseY = mouseY;
			return;
		}
		player.mouseMoved(mouseX - lastMouseX, mouseY - lastMouseY);
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		
	}
}
