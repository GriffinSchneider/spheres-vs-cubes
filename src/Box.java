
import java.awt.Color;
import java.util.Vector;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;


public class Box extends PObject {
	protected Vector3f dim;

    private Vector3f initialPos;
    private float initialMass;
    private float horizontalRotation;
    private float verticalRotation;
	
    public Box(Vector3f pos, Vector3f dim_, float mass, Color color_, DiscreteDynamicsWorld world_, SpheresVsCubes applet_) {
        this(pos, dim_, 0, 0, mass, color_, world_, applet_);
	}
	
    public Box(Vector3f pos, Vector3f dim_, float horizontalRotation, float verticalRotation, float mass, Color color_, DiscreteDynamicsWorld world_, SpheresVsCubes applet_) {
		super(color_, world_, applet_);
        this.initialPos = pos;
		dim = dim_;
        this.horizontalRotation = horizontalRotation;
        this.verticalRotation = verticalRotation;
        this.initialMass = mass;
        this.makeShape();
	}

    private void makeShape() {
		addShape(initialPos, 
                 initialMass, 
                 new BoxShape(new Vector3f(dim.x / (2*GRAPHICS_UNITS_PER_PHYSICS_UNITS), 
                                           dim.y / (2*GRAPHICS_UNITS_PER_PHYSICS_UNITS), 
                                           dim.z / (2*GRAPHICS_UNITS_PER_PHYSICS_UNITS))),
                 horizontalRotation,
                 verticalRotation);
    }

    public void shift(Vector3f shift) {
        this.remove();
        this.body.destroy();
        this.initialPos.add(shift);
        makeShape();
    }

    public void grow(Vector3f grow) {
        this.remove();
        this.body.destroy();
        this.dim.add(grow);
        makeShape();
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
