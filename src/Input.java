

public class Input {
	private static boolean[] keys = new boolean[526];
	
	public static boolean checkKey(int keyCode) {
		if (keys.length >= keyCode) {
			return keys[keyCode];  
		}
		return false;
	}

	public static void keyPressed(int keyCode) {
		keys[keyCode] = true;
	}

	public static void keyReleased(int keyCode) { 
		keys[keyCode] = false; 
	}
}
