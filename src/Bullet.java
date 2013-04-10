

import java.awt.Color;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;


public class Bullet extends Box {
	private static final int LIFE_SPAN = 150;
	private int count;
	
	public Bullet(Vector3f pos_, DiscreteDynamicsWorld world_, SpheresVsCubes applet_) {
		super(pos_, new Vector3f(5, 5, 5), 0.5f, Color.ORANGE, world_, applet_);
		count = 0;
	}
	
	@Override
	public void update() {
		if (!applet.isEditorMode && count++ > LIFE_SPAN) {
			remove();
		}
	}

}
