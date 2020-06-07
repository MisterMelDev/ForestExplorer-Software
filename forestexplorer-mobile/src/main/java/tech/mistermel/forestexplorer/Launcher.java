package tech.mistermel.forestexplorer;

public class Launcher {

	private static Robot instance;
	
	public static void main(String[] args) {
		instance = new Robot();
		instance.start();
	}
	
	public static Robot instance() {
		return instance;
	}

}
