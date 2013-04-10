

import java.awt.Color;

import javax.vecmath.Vector3f;

public class Enemy extends Box {
	private static final int BULLET_SPEED = 5;
	private static final int SHOT_DELAY = 50;
	private int count;
	private PObject target;
	
	public Enemy(PObject target_, Vector3f pos_, SpheresVsCubes applet_) {
		super(pos_, new Vector3f(20, 20, 20), 2, Color.RED, applet_);
		target = target_;
		count = 0;
	}
	
	@Override
	public void update() {
		if (count++ > SHOT_DELAY && !applet.isEditorMode) {
			count = 0;
			
			if (target != null) {
				Vector3f pos = getGraphicsPos();
				
				Vector3f norm = target.getGraphicsPos();
				norm.sub(pos);
				norm.normalize();
				
				Vector3f bulletPos = new Vector3f(norm.x * dim.x, norm.y * dim.y + dim.y / 2, norm.z * dim.z);
				bulletPos.add(pos);
				
				Bullet bullet = new Bullet(bulletPos, applet);
				
				Vector3f impulse = new Vector3f(norm);
				impulse.scale(BULLET_SPEED);
	
				bullet.body.applyCentralImpulse(impulse);
			}
		}
	}

}
