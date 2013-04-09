package Objects;

import java.awt.Color;

import javax.vecmath.Vector3f;

import processing.core.PApplet;

public class Bullet extends Box {

	public Bullet(Vector3f pos_, PApplet applet_) {
		super(pos_, new Vector3f(5, 5, 5), 2, Color.ORANGE, applet_);
	}
	
	@Override
	public void update() {
		
	}

}
