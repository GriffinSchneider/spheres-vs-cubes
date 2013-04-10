

public class Input {
	private static boolean[] keys = new boolean[526];
	private static boolean mouseClicked = false;
	
	public static boolean checkKey(int keyCode) {
		if (keys.length >= keyCode) {
			return keys[keyCode];  
		}
		return false;
	}
	
	public static boolean isMouseClicked() {
		return mouseClicked;
	}
	
	public static void mousePressed() {
		mouseClicked = true;
	}
	
	public static void mouseReleased() {
		mouseClicked = false;
	}

	public static void keyPressed(int keyCode) {
		keys[keyCode] = true;
	}

	public static void keyReleased(int keyCode) { 
		keys[keyCode] = false; 
	}
}
