
import java.awt.Color;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;


public class Box extends PObject {
	protected Vector3f dim;
	
	public Box(Vector3f pos, Vector3f dim_, float mass, Color color_, SpheresVsCubes applet_) {
		this(pos, dim_, 0, new Vector3f(0, 1, 0), mass, color_, applet_);
	}
	
	public Box(Vector3f pos, Vector3f dim_, float rotationAngle, Vector3f rotationAxis, float mass, Color color_, SpheresVsCubes applet_) {
		super(color_, applet_);
		dim = dim_;
		addShape(pos, 
				mass, 
				new BoxShape(new Vector3f(dim.x / (2*GRAPHICS_UNITS_PER_PHYSICS_UNITS), 
										  dim.y / (2*GRAPHICS_UNITS_PER_PHYSICS_UNITS), 
										  dim.z / (2*GRAPHICS_UNITS_PER_PHYSICS_UNITS))),
				rotationAngle,
				rotationAxis);
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
