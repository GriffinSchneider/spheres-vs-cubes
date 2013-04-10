

import java.awt.Color;
import java.util.ArrayList;

import javax.vecmath.Vector2f;

import processing.core.PConstants;

public class Button extends Node {
    private static ArrayList<Button> buttonList = new ArrayList<Button>();
    
    public static void removeButtons() {
        buttonList.clear();
    }
    
    public static void updateButtons() {
        for (int i = buttonList.size()-1; i >=0; i--) {
            buttonList.get(i).update();
        }
    }
    
    public static void displayButtons() {
        for (int i = buttonList.size()-1; i >=0; i--) {
            Button b = buttonList.get(i);
            b.draw();
        }
    }
    
    private static void uncheckButtons() {
        // Uncheck all checkboxes in the current scene
        for (int i = buttonList.size()-1; i >=0; i--) {
            Button b = buttonList.get(i);
            if (b.isCheckbox) b.isDown = false;
        }
    }
    
    public static Button createButton(Vector2f pos_, float w_, float h_, ButtonCallback c_, SpheresVsCubes applet_) {
        Button b = new Button(pos_, w_, h_, c_, applet_);
        return b;
    }
    
    public static Button createCheckBox(Vector2f pos_, float w_, float h_, ButtonCallback c_, SpheresVsCubes applet_) {
        Button b = new Button(pos_, w_, h_, c_, applet_);
        b.isCheckbox = true;
        return b;
    }
    Vector2f pos;
    float w, 
          h;
    String text;
    Color fill,
          fillDown,
          stroke;
    private boolean isDown;
    private boolean isCheckbox;
    
    private ButtonCallback callback;
    
    Button(Vector2f pos_, float w_, float h_, ButtonCallback c_, SpheresVsCubes applet_) {
    	super(applet_);
        this.pos = pos_;
        
        this.text = "";
        this.w = w_;
        this.h = h_;
        this.fill = new Color(100, 100, 255);
        this.fillDown = Color.CYAN;
        this.stroke = Color.BLACK;
        this.isDown = false;
        this.isCheckbox = false;
        this.callback = c_;
        
        buttonList.add(this);
    }

    public void draw() {
        applet.pushStyle();
        
        if (this.isDown) applet.fill(this.fillDown.getRGB());
        else applet.fill(this.fill.getRGB());
        
        applet.stroke(this.stroke.getRGB());
        applet.rectMode(PConstants.CORNER);
        applet.rect(pos.x, pos.y, w, h);
        
        applet.fill(0);
        applet.textSize(15);
        applet.textAlign(PConstants.CENTER);
        applet.text(this.text, pos.x + this.w / 2, pos.y + this.h / 2);
        
        applet.popStyle();
    }

    public void update() {
        if (!isCheckbox) this.isDown = false;
        
        if (Input.isMouseClicked()) {
            if(applet.mouseX > pos.x && applet.mouseX <pos.x+w && 
            		applet.mouseY > pos.y && applet.mouseY <pos.y+h) {
                if (!this.isDown && this.isCheckbox) Button.uncheckButtons();
                
                this.isDown = !this.isDown;
                
                try {
                    callback.isDown = this.isDown;
                    callback.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean isDown() {
        return isDown;
    }

    public void destroy() {
        buttonList.remove(this);
    }
}
