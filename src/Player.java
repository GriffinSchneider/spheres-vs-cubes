

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import processing.core.PApplet;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;


public class Player extends Sphere {
	// Initial radius in graphics units of the player sphere
    public static final float PLAYER_INITIAL_RADIUS = 10;
    // Magnitude of impulse to apply each frame in the x-direction to make the player move
    public static final float PLAYER_MOVEMENT_IMPULSE = 20 / GRAPHICS_UNITS_PER_PHYSICS_UNITS;
    // Maximum x-velocity that the player can reach before we stop increasing the velocity
    // due to key presses.
    public static final float PLAYER_MAX_SPEED = 140 / GRAPHICS_UNITS_PER_PHYSICS_UNITS;
    // Amount to decrease the player's x-velocity each frame if no movement buttons
    // are being pressed.
    public static final float PLAYER_NO_MOVEMENT_DAMPING = 4 / GRAPHICS_UNITS_PER_PHYSICS_UNITS;
    // Magnitude of impulse in the y-direction to apply to make the player "jump"
    public static final float PLAYER_JUMP_IMPULSE = 200 / GRAPHICS_UNITS_PER_PHYSICS_UNITS;
	
	private float horizontalRotation;
	private float verticalRotation;
	private boolean canJump;
	private Vector3f editorModeMovementOffset;
	
	public Player(Vector3f pos_, DiscreteDynamicsWorld world_, SpheresVsCubes applet_) {
		super(pos_, PLAYER_INITIAL_RADIUS, 2, Color.GREEN, world_, applet_);
		horizontalRotation = 0;
		body.setFriction(0.8f);
		canJump = false;
	}
	
	public float getHorizontalRotation() {
		return horizontalRotation;
	}
	
	public float getVerticalRotation() {
		return verticalRotation;
	}

	// In editor mode, we must update the position without
	// updating the real physics position until we exit editor mode
	// (since the physics position of a body won't actually change until
	// we step the simulation)
	@Override 
	public Vector3f getPhysicsPos() {
		Vector3f physicsPos = super.getPhysicsPos();
		if (editorModeMovementOffset != null && applet.isEditorMode) {
			physicsPos.add(editorModeMovementOffset);
		}
		return physicsPos;
	}
	
	@Override
	public void setRadius(float radius_) {
		super.setRadius(radius_);
		if (radius_ <= 0) {
			applet.changeScene(new MenuScene(applet));
		}
	}

	@Override
	public void onCollision(PObject object) {
		if (object instanceof Bullet) {
			object.remove();
			this.setRadius(this.radius - 1);
		}
		else {
			canJump = true;
		}
	}
	
	@Override
	public void update() {
        // Rotate the player horizontally
        if (Input.checkKey(KeyEvent.VK_A) || Input.checkKey(PApplet.LEFT)) {
            horizontalRotation -= PApplet.radians(3);
        } else if (Input.checkKey(KeyEvent.VK_D) || Input.checkKey(PApplet.RIGHT)) {
            horizontalRotation += PApplet.radians(3);
        }
        
        // Rotate the player up/down
        if (Input.checkKey(KeyEvent.VK_Z)) {
        	verticalRotation -= PApplet.radians(3);
        } else if (Input.checkKey(KeyEvent.VK_X)) {
        	verticalRotation += PApplet.radians(3);
        }
        
        // Clamp vertical rotation so you can't look past 'all the way' up or down.
        // Letting the angle actually get to pi or 0 breaks stuff.
        if (verticalRotation > Math.PI - 0.001f) verticalRotation = (float)Math.PI - 0.001f;
        if (verticalRotation < 0.001f) verticalRotation = 0.001f;

        boolean isForwardPressed  = Input.checkKey(KeyEvent.VK_W) || Input.checkKey(PApplet.UP);
        boolean isBackwardPressed = Input.checkKey(KeyEvent.VK_S) || Input.checkKey(PApplet.DOWN);
        float movementImpulseX = PApplet.cos(this.horizontalRotation) * PLAYER_MOVEMENT_IMPULSE; 
        float movementImpulseZ = PApplet.sin(this.horizontalRotation) * PLAYER_MOVEMENT_IMPULSE;
        
        if (applet.isEditorMode) {
        	// Move forward/backward
            if (isForwardPressed) {
            	this.editorModeMovementOffset.add(new Vector3f(-0.3f*movementImpulseX, 0, -0.3f*movementImpulseZ));
            } else if (isBackwardPressed) {
            	this.editorModeMovementOffset.add(new Vector3f(0.3f*movementImpulseX, 0, 0.3f*movementImpulseZ));
            }
            // Move up/down
            if (Input.checkKey(KeyEvent.VK_SPACE)) {
            	this.editorModeMovementOffset.add(new Vector3f(0, -PLAYER_MOVEMENT_IMPULSE*0.3f, 0));
            } else if (Input.checkKey(KeyEvent.VK_BACK_SPACE)) {
            	this.editorModeMovementOffset.add(new Vector3f(0, PLAYER_MOVEMENT_IMPULSE*0.3f, 0));
            }

        } else {
            // Move the player
            Vector3f currentVelocity = body.getLinearVelocity(new Vector3f());
            float currentSpeed = PApplet.sqrt(PApplet.sq(currentVelocity.x) + PApplet.sq(currentVelocity.z));
		
            if (isForwardPressed && currentSpeed < PLAYER_MAX_SPEED) {
                body.applyCentralImpulse(new Vector3f(-movementImpulseX, 0, -movementImpulseZ));
            } else if (isBackwardPressed && currentSpeed < PLAYER_MAX_SPEED) {
                body.applyCentralImpulse(new Vector3f(movementImpulseX, 0, movementImpulseZ));
            }
            else {
                Vector3f normalized = new Vector3f(currentVelocity);
                normalized.normalize();
                float dampX = PLAYER_NO_MOVEMENT_DAMPING * normalized.x;
                float dampZ = PLAYER_NO_MOVEMENT_DAMPING * normalized.z;
			
                body.applyCentralImpulse(new Vector3f(-dampX, 0, -dampZ));
            }
            // Make the player jump
            if (Input.checkKey(KeyEvent.VK_SPACE) && canJump) {
                body.applyCentralImpulse(new Vector3f(0, PLAYER_JUMP_IMPULSE, 0));
                canJump = false;
            }
        }
	}
	
	// Hack: these methods get called from keyPressed, since we only
	// want them to happen once when the key is pressed down.
	public void toggleEditorMode() {
		if (!this.applet.isEditorMode) {
			this.applet.isEditorMode = true;
			this.editorModeMovementOffset = new Vector3f(0, 0, 0);
		} else {
			this.applet.isEditorMode = false;
			editorModeMovementOffset.y = -editorModeMovementOffset.y;
			this.body.translate(editorModeMovementOffset);
			this.editorModeMovementOffset = null;
		}
	}
	
	// Place rectangle slightly in front of the player if editing
	public void placeRectangle() {
		if (applet.isEditorMode) {
			Vector3f pos = getGraphicsPos();
	    	pos.add(new Vector3f(-PApplet.cos(this.horizontalRotation)*20, 0, -PApplet.sin(this.horizontalRotation)*20));
	    	Box b = new Box(pos, 
		        			new Vector3f(5, 200, 200),
		        			-horizontalRotation,
		        			verticalRotation - (float)Math.PI/2,
		        			0, 
		        			Color.GREEN,
		        			this.world,
		        			this.applet);
		}
	}
}

