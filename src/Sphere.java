import java.awt.Color;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.SphereShape;

import processing.core.PApplet;


public class Sphere extends PObject {
	private float radius;
	
	Sphere(Vector3f pos_, float radius_, float mass_, Color color_, PApplet applet_) {
		super(pos_, mass_, color_, applet_);
		radius = radius_;
		addShape(new SphereShape(radius));
	}

	@Override
	public void draw() {
		applet.fill(color.getRGB());
		applet.sphere(radius);
	}

}
