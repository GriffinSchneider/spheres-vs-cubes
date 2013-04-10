
import java.awt.Color;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;


public class Box extends PObject {
	protected Vector3f dim;
	
	public Box(Vector3f pos, Vector3f dim_, float mass, Color color_, DiscreteDynamicsWorld world_, SpheresVsCubes applet_) {
		super(color_, world_, applet_);
		dim = dim_;
		addShape(pos, mass, new BoxShape(new Vector3f(
				dim.x / (2*GRAPHICS_UNITS_PER_PHYSICS_UNITS), 
				dim.y / (2*GRAPHICS_UNITS_PER_PHYSICS_UNITS), 
				dim.z / (2*GRAPHICS_UNITS_PER_PHYSICS_UNITS))));
	}

	@Override
	public void draw() {
		applet.fill(color.getRGB());
		applet.box(dim.x, dim.y, dim.z);
	}

	@Override
	public void update() {
	}

	@Override
	public void onCollision(PObject object) {
	}
}
