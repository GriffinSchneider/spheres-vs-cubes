package Objects;

import java.awt.Color;

import javax.vecmath.Vector3f;

import processing.core.PApplet;

public class Enemy extends Box {

	public Enemy(Vector3f pos_, PApplet applet_) {
		super(pos_, new Vector3f(20, 20, 20), 2, Color.RED, applet_);
	}
	
	@Override
	public void update() {
		
	}

}
