

import java.awt.Color;

import javax.vecmath.Vector3f;

public class Enemy extends Box {
	private static final int SHOT_DELAY = 50;
	private int count;
	
	public Enemy(Vector3f pos_, SpheresVsCubes applet_) {
		super(pos_, new Vector3f(20, 20, 20), 2, Color.RED, applet_);
		count = 0;
	}
	
	@Override
	public void update() {
		if (count++ > SHOT_DELAY) {
			count = 0;
			
			Bullet bullet = new Bullet(this.getGraphicsPos(), applet);
			// Todo shoot at player
			bullet.body.applyCentralImpulse(new Vector3f(5, 5, 5));
		}
	}

}
