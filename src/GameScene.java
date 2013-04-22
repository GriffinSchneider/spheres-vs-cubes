import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.vecmath.Vector3f;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
	
	private String lastFileLoaded;
	
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
		int maxProxies = 2000;
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);

		// the default constraint solver. For parallel processing you can use a
		// different solver (see Extras/BulletMultiThreaded)
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0, -WORLD_GRAVITY, 0));

		player = new Player(new Vector3f(0, 250, 0), dynamicsWorld, applet, this);
    }
	
	@Override
	public void draw() {
		super.draw();
		
		// Do physics simulation
		if (!applet.isEditorMode) {
			dynamicsWorld.stepSimulation(1.f / 60.f, 25);
			checkCollisions();
		}

        checkKeys();
		
		
		applet.background(0, 0, 0);
		applet.translate(applet.width / 2f, applet.height / 2f, 0);
		
		// Print FPS every 120 frames (ideally, 2 seconds)
		if (applet.frameCount % 200 == 0) {
			System.out.println("" + applet.frameRate + " FPS");
		}
		
		Vector3f playerPos = player.getGraphicsPos();
		float playerHorizontalRotation = player.horizontalRotation;
		float playerVerticalRotation = player.verticalRotation;
		
		// applet.perspective(PApplet.radians(60), applet.width / applet.height, 0.01f, 5000);
		// applet.ortho(0, applet.width, 0, applet.height, -1000, 1000); // This looks really cool
		
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
	
	// Some nastyness to prevent "References to generic type HashMap should be parameterized"
    // warnings in saveLevel. This way, we don't need to @SuppressWarnings("unchecked") the
    // entire method and possibly lose some real warnings.
    @SuppressWarnings("unchecked")
	private static Map<Object, Object> asMap(JSONObject j) {
		return j;
	}
	@SuppressWarnings("unchecked")
	private static ArrayList<Object> asArrayList(JSONArray j) {
		return j;
	}
	
	private void saveLevel() {
		JSONArray level = new JSONArray();
		
		for (int j=dynamicsWorld.getNumCollisionObjects()-1; j>=0; j--) {
			CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(j);
			
			RigidBody body = RigidBody.upcast(obj);
			if (body != null) {
				PObject pobj;
				if ((pobj = (PObject) body.getUserPointer()) != null &&
				     pobj.getClass() != Bullet.class) {
					JSONObject object = new JSONObject();
					Vector3f pos = pobj.getGraphicsPos();
					asMap(object).put("x", pos.x);
					asMap(object).put("y", pos.y);
					asMap(object).put("z", pos.z);
					asMap(object).put("class", pobj.getClass().toString());
					if (pobj instanceof Box) {
						Box box = (Box)pobj;
						
						Vector3f dim = box.dim;
						asMap(object).put("dx", dim.x);
						asMap(object).put("dy", dim.y);
						asMap(object).put("dz", dim.z);
						
						asMap(object).put("vr", box.initialVerticalRotation);
						asMap(object).put("hr", box.initialHorizontalRotation);
					}
					asArrayList(level).add(object);
				}
			}
		}
		
	    try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception e) { 
            e.printStackTrace();  
        } 
 
        // create a file chooser 
        JFileChooser fc; 
        if (lastFileLoaded == null)
            fc = new JFileChooser("levels/"); 
        else {
            fc = new JFileChooser(lastFileLoaded); 
        }
 
        // in response to a button click: 
        int returnVal = fc.showSaveDialog(null); 
 
        if (returnVal == JFileChooser.APPROVE_OPTION) { 
            File file = fc.getSelectedFile(); 
            lastFileLoaded = file.getAbsolutePath();
            try {
                FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
                fileWriter.write(level.toJSONString());
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	public void loadLevel() {
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception e) { 
            e.printStackTrace();  
        } 
 
        // create a file chooser 
        JFileChooser fc; 
        if (lastFileLoaded == null)
            fc = new JFileChooser("levels/"); 
        else {
            fc = new JFileChooser(lastFileLoaded); 
        }
 
        // in response to a button click: 
        int returnVal = fc.showOpenDialog(null);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) { 
            File file = fc.getSelectedFile(); 
            lastFileLoaded = file.getAbsolutePath();
            GameScene nextLevel = new GameScene(applet);
			applet.changeScene(nextLevel);
			nextLevel.loadLevel(file.getAbsolutePath());
        }
    }
	
    public void loadLevel(String slevel) {
        JSONParser parser = new JSONParser();
        lastFileLoaded = slevel;

        try {
            Object list = parser.parse(new FileReader(slevel));
            JSONArray level = (JSONArray) list;
            for (Object obj : level.toArray()) {
                JSONObject node =  (JSONObject) obj;

                String sClass = (String) node.get("class");
                float x = ((Double) node.get("x")).floatValue();
                float y = ((Double) node.get("y")).floatValue();
                float z = ((Double) node.get("z")).floatValue();
                Vector3f pos = new Vector3f(x, y, z);
                if (sClass.equals(Box.class.toString())) {
                    float dx = ((Double) node.get("dx")).floatValue();
                    float dy = ((Double) node.get("dy")).floatValue();
                    float dz = ((Double) node.get("dz")).floatValue();
                    float vr = ((Double) node.get("vr")).floatValue();
                    float hr = ((Double) node.get("hr")).floatValue();
                    
                    new Box(pos, 
                    		new Vector3f(dx, dy, dz),
                    		hr,
                    		vr,
                			0, 
                			Color.GRAY,
                			dynamicsWorld,
                			applet);
                }
                else if (sClass.equals(Enemy.class.toString())) {
                	new Enemy(player, pos, dynamicsWorld, applet);
                }
                else if (sClass.equals(EndPoint.class.toString())) {
                    new EndPoint(pos, dynamicsWorld, applet);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    public void loadNextLevel() {
    	try {
    		// Attempt to find a number at the end of the file name, increment that number 
    		// by one, and use the result as a filename to find the next level file. If 
    		// we can't find the next level, you win.
    		Pattern p =  Pattern.compile(".*?([0-9]+)");
        	Matcher m = p.matcher(lastFileLoaded);
        	m.find();
        	String number = m.group(1);
    		int i = Integer.parseInt(number);
    		String toLoad = lastFileLoaded.substring(0, m.start(1)) + (i+1);
    		if (new File(toLoad).exists()) {
				GameScene nextLevel = new GameScene(applet);
				applet.changeScene(nextLevel);
				nextLevel.loadLevel(toLoad);
				// Found the next level!
				return;
    		} else {
    			System.out.println("Next level not found: " + toLoad);
    		}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	// Failed to find the next level for some reason
    	applet.changeScene(new GameOverScene(applet));
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

    private Box lastPlacedBox;
    public void checkKeys() {
        // Zoom camera
		if (Input.checkKey(KeyEvent.VK_EQUALS)) {
			cameraDistance -= 5;
		} else if (Input.checkKey(KeyEvent.VK_MINUS)) {
			cameraDistance += 5;
		}
		if (cameraDistance < 0) cameraDistance = 0;
        
        if (applet.isEditorMode) {

            // Hold backslash or quote to make all changes happen faster or slower
            float changeAmount = 1;
            if (Input.checkKey(KeyEvent.VK_BACK_SLASH)) {
                changeAmount = 4;
            } else if (Input.checkKey(KeyEvent.VK_QUOTE)) {
                changeAmount = 0.3f;
            }
            
            // Shift the previously placed box with (shift +) Y, H, N
            Vector3f shiftVector = new Vector3f(0, 0, 0);
            if (Input.checkKey(KeyEvent.VK_Y)) {
                shiftVector.add(new Vector3f(changeAmount, 0, 0));
            }
            if (Input.checkKey(KeyEvent.VK_H)) {
                shiftVector.add(new Vector3f(0, changeAmount, 0));
            }
            if (Input.checkKey(KeyEvent.VK_N)) {
                shiftVector.add(new Vector3f(0, 0, changeAmount));
            }
            if (Input.checkKey(KeyEvent.VK_SHIFT)) {
                shiftVector.scale(-1);
            }
            if (!shiftVector.equals(new Vector3f(0, 0, 0)) && this.lastPlacedBox != null) {
                this.lastPlacedBox.shift(shiftVector);
            }

            // Scale the previously placed box with (shift +) U, J, M
            Vector3f growVector = new Vector3f(0, 0, 0);
            if (Input.checkKey(KeyEvent.VK_U)) {
                growVector.add(new Vector3f(changeAmount, 0, 0));
            }
            if (Input.checkKey(KeyEvent.VK_J)) {
                growVector.add(new Vector3f(0, changeAmount, 0));
            }
            if (Input.checkKey(KeyEvent.VK_M)) {
                growVector.add(new Vector3f(0, 0, changeAmount));
            }
            if (Input.checkKey(KeyEvent.VK_SHIFT)) {
                growVector.scale(-1);
            }
            if (!growVector.equals(new Vector3f(0, 0, 0)) && this.lastPlacedBox != null) {
                this.lastPlacedBox.grow(growVector);
            }

            // Snap the horizontal angle to cardinal directions with (shift +) I, K
            if (Input.checkKey(KeyEvent.VK_I)) {
                if (Input.checkKey(KeyEvent.VK_SHIFT)) {
                    player.horizontalRotation = (float)Math.PI;
                } else {
                    player.horizontalRotation = 0;
                }
            } else if (Input.checkKey(KeyEvent.VK_K)) {
                if (Input.checkKey(KeyEvent.VK_SHIFT)) {
                    player.horizontalRotation = (float)Math.PI/2;
                } else {
                    player.horizontalRotation = -(float)Math.PI/2;
                }
            }

            // Snap the vertical angle to up/down with (shift + ) comma.
            if (Input.checkKey(KeyEvent.VK_COMMA)) {
                if (Input.checkKey(KeyEvent.VK_SHIFT)) {
                    player.verticalRotation = (float)Math.PI - 0.0001f;
                } else {
                    player.verticalRotation = 0.0001f;
                }
            }

            // Snap the vertical angle to looking horizontally with o.
            if (Input.checkKey(KeyEvent.VK_O)) {
                player.verticalRotation = (float)Math.PI/2;
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
			lastPlacedBox = this.player.placeRectangle();
		} else if (keyCode == KeyEvent.VK_T) {
            this.player.placeEnemy();
		} else if (keyCode == KeyEvent.VK_G) {
			this.player.placeEndPoint();
		} else if (keyCode == KeyEvent.VK_P) {
        	this.saveLevel();
        } else if (keyCode == KeyEvent.VK_SEMICOLON) {
        	this.loadLevel();
        } else if (keyCode == KeyEvent.VK_CLOSE_BRACKET) {
        	this.loadNextLevel();
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
