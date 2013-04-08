package Objects;
import java.awt.Color;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.SphereShape;

import processing.core.PApplet;


public class Sphere extends PObject {
	private float radius;
	
	public Sphere(Vector3f pos_, float radius_, float mass_, Color color_, PApplet applet_) {
		super(pos_, mass_, color_, applet_);
		radius = radius_;
		addShape(new SphereShape(radius / GRAPHICS_UNITS_PER_PHYSICS_UNITS));
	}

	@Override
	public void draw() {
		//applet.noStroke();
		applet.strokeWeight(0.05f);
		applet.fill(color.getRGB());
		applet.sphere(radius);
		applet.strokeWeight(1f);
		//applet.stroke(0);
	}

	@Override
	public void update() {
	}

	@Override
	public void onCollision(PObject object) {
	}

}
