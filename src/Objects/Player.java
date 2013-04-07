package Objects;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.vecmath.Vector3f;

import processing.core.PApplet;

import Utils.Input;

public class Player extends Sphere {
	// Initial radius in graphics units of the player sphere
    public static final float PLAYER_INITIAL_RADIUS = 12f;
    // Magnitude of impulse to apply each frame in the x-direction to make the player move
    public static final float PLAYER_MOVEMENT_IMPULSE = 0.2f;
    // Maximum x-velocity that the player can reach before we stop increasing the velocity
    // due to key presses.
    public static final float PLAYER_MAX_SPEED = 1.5f;
    // Amount to decrease the player's x-velocity each frame if no movement buttons
    // are being pressed.
    public static final float PLAYER_NO_MOVEMENT_DAMPING = 0.08f;
    // Magnitude of impulse in the y-direction to apply to make the player "jump"
    public static final float PLAYER_JUMP_IMPULSE = 0.8f;
	
	private float rotation;
	
	public Player(Vector3f pos_, PApplet applet_) {
		super(pos_, PLAYER_INITIAL_RADIUS, 2, Color.GREEN, applet_);
		rotation = 0;
		body.setFriction(0.8f);
	}
	
	public float getRotation() {
		return rotation;
	}

	@Override
	public void update() {
		// Rotate the player
		if (Input.checkKey(KeyEvent.VK_A) || Input.checkKey(PApplet.LEFT)) {
			rotation -= PApplet.radians(3);
		}
		else if (Input.checkKey(KeyEvent.VK_D) || Input.checkKey(PApplet.RIGHT)) {
			rotation += PApplet.radians(3);
		}
		
		// Move the player
		Vector3f currentVelocity = body.getLinearVelocity(new Vector3f());
		float currentSpeed = PApplet.sqrt(PApplet.sq(currentVelocity.x) + PApplet.sq(currentVelocity.z));
		float movementImpulseX = PApplet.cos(this.rotation) * PLAYER_MOVEMENT_IMPULSE; 
		float movementImpulseZ = PApplet.sin(this.rotation) * PLAYER_MOVEMENT_IMPULSE;
		
		boolean isForwardPressed  = Input.checkKey(KeyEvent.VK_W) || Input.checkKey(PApplet.UP);
		boolean isBackwardPressed = Input.checkKey(KeyEvent.VK_S) || Input.checkKey(PApplet.DOWN);
		
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
		if (Input.checkKey(KeyEvent.VK_SPACE)) {
			body.applyCentralImpulse(new Vector3f(0, PLAYER_JUMP_IMPULSE, 0));
		}
	}
}