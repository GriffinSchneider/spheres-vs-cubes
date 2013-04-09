
import java.awt.Color;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.SphereShape;


public class Sphere extends PObject {
	protected float radius;
	
	public Sphere(Vector3f pos, float radius_, float mass, Color color_, SpheresVsCubes applet_) {
		super(color_, applet_);
		radius = radius_;
		addShape(pos, mass, new SphereShape(radius / GRAPHICS_UNITS_PER_PHYSICS_UNITS));
	}

	@Override
	public void draw() {
		applet.strokeWeight(0.05f);
		applet.fill(color.getRGB());
		applet.sphere(radius);
		applet.strokeWeight(1f);
	}
	
	public void setRadius(float radius_) {
		if (radius > 0) {
			radius = radius_;
			this.body.setCollisionShape(new SphereShape(radius / GRAPHICS_UNITS_PER_PHYSICS_UNITS));
		}
	}
	
	@Override
	public void update() {
	}

	@Override
	public void onCollision(PObject object) {
	}

}
