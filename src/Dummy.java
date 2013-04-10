import java.awt.Color;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;


public class Dummy extends PObject {
	private Vector3f pos;
	
	Dummy(Vector3f pos_, DiscreteDynamicsWorld world_, SpheresVsCubes applet_) {
		super(Color.WHITE, world_, applet_);
		pos = pos_;
	}
	
	@Override
	public Vector3f getPhysicsPos() {
		Vector3f tmp = getGraphicsPos();
		tmp.scale(1f / GRAPHICS_UNITS_PER_PHYSICS_UNITS);
		tmp.y = -tmp.y;
		return tmp;
	}
	
	@Override
	public Vector3f getGraphicsPos() {
		return new Vector3f(pos);
	}

	@Override
	public void onCollision(PObject object) {
	}

	@Override
	public void draw() {
	}

	@Override
	public void update() {
	}

}
