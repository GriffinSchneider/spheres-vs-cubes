import java.awt.Color;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;


public class EndPoint extends Sphere {

	public EndPoint(Vector3f pos, DiscreteDynamicsWorld world_, SpheresVsCubes applet_) {
		super(pos, 20, 0, Color.BLUE, world_, applet_);
	}

}
