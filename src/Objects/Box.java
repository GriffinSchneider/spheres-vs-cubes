package Objects;
import java.awt.Color;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;

import processing.core.PApplet;


public class Box extends PObject {
	private Vector3f dim;
	
	public Box(Vector3f pos_, Vector3f dim_, float mass_, Color color_, PApplet applet_) {
		super(pos_, mass_, color_, applet_);
		dim = dim_;
		addShape(new BoxShape(new Vector3f(
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
