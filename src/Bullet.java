

import java.awt.Color;

import javax.vecmath.Vector3f;


public class Bullet extends Box {
	private static final int LIFE_SPAN = 150;
	private int count;
	
	public Bullet(Vector3f pos_, SpheresVsCubes applet_) {
		super(pos_, new Vector3f(5, 5, 5), 2, Color.ORANGE, applet_);
		count = 0;
	}
	
	@Override
	public void update() {
		if (count++ > LIFE_SPAN) {
			remove();
		}
	}

}
