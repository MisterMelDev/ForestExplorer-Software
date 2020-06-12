package tech.mistermel.forestexplorer;

import processing.core.PApplet;
import tech.mistermel.forestexplorer.application.InfoDisplay;

public class Launcher {
	
	public static InfoDisplay infoDisplay;
	public static long startTime;
	
	public static void main(String[] args) {
		startTime = System.currentTimeMillis();
		infoDisplay = new InfoDisplay();
		PApplet.runSketch(new String[] { "InfoDisplay" }, infoDisplay);
	}
	
}
