package Objects;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.vecmath.Vector3f;

import Utils.Input;

import processing.core.PApplet;

public class Player extends Sphere {
	// Initial radius (in physics units) of the player circle
    public static final float PLAYER_INITIAL_RADIUS = 12f;
    // Magnitude of impulse to apply each frame in the x-direction to make the player move
    public static final float PLAYER_MOVEMENT_IMPULSE = 13f;
    // Maximum x-velocity that the player can reach before we stop increasing the velocity
    // due to key presses.
    public static final float PLAYER_MAX_SPEED = 50f;
    // Amount to decrease the player's x-velocity each frame if no movement buttons
    // are being pressed.
    public static final float PLAYER_NO_MOVEMENT_DAMPING = 8f;
    // Magnitude of impulse in the y-direction to apply to make the player "jump"
    public static final float PLAYER_JUMP_IMPULSE = 2f;
	
	private float rotation;
	
	public Player(Vector3f pos_, PApplet applet_) {
		super(pos_, PLAYER_INITIAL_RADIUS, 1, Color.GREEN, applet_);
		rotation = 0;
	}
	
	public float getRotation() {
		return rotation;
	}

	@Override
	public void update() {
		// Rotate the player
		if (Input.checkKey(KeyEvent.VK_A) || Input.checkKey(PApplet.LEFT)) {
			rotation-=3;
		}
		else if (Input.checkKey(KeyEvent.VK_D) || Input.checkKey(PApplet.RIGHT)) {
			rotation+=3;
		}
		float rads = PApplet.radians(rotation);
		
		// Move the player
		Vector3f velocity = body.getLinearVelocity(new Vector3f());
		float currentSpeed = PApplet.sqrt(PApplet.sq(velocity.x) + PApplet.sq(velocity.z));
		float speedX = PLAYER_MOVEMENT_IMPULSE * PApplet.cos(rads);
		float speedZ = PLAYER_MOVEMENT_IMPULSE * PApplet.sin(rads);
		if ((Input.checkKey(KeyEvent.VK_W) || Input.checkKey(PApplet.UP)) && currentSpeed < PLAYER_MAX_SPEED) {
			body.applyCentralImpulse(new Vector3f(-speedX, 0, -speedZ));
		}
		else if ((Input.checkKey(KeyEvent.VK_S) || Input.checkKey(PApplet.DOWN)) && currentSpeed < PLAYER_MAX_SPEED) {
			body.applyCentralImpulse(new Vector3f(speedX, 0, speedZ));
		}
		else {
			Vector3f normalized = new Vector3f(velocity);
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
