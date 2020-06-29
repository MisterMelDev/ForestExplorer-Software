package tech.mistermel.forestexplorer;

public class Launcher {

	public static Robot instance;
	public static boolean debugMode;
	
	public static void main(String[] args) {
		if(args.length > 0) {
			debugMode = args[0].equals("--dev");
		}
		
		instance = new Robot();
	}
	
}
