package tech.mistermel.forestexplorer.controller;

public enum MovementDirection {

	STATIONARY(0, 0),
	FORWARDS(1, 1), BACKWARDS(-1, -1), LEFT(-1, 1), RIGHT(1, -1),
	FORWARDS_LEFT(-1, 1), FORWARDS_RIGHT(1, -1),
	BACKWARDS_LEFT(-1, 1), BACKWARDS_RIGHT(1, -1);
	
	private int leftMotorDir, rightMotorDir;
	
	private MovementDirection(int leftMotorDir, int rightMotorDir) {
		this.leftMotorDir = leftMotorDir;
		this.rightMotorDir = rightMotorDir;
	}
	
	public int getLeftMotorDir() {
		return leftMotorDir;
	}
	
	public int getRightMotorDir() {
		return rightMotorDir;
	}
	
	public static MovementDirection getFromWebName(String str) {
		switch(str) {
			case "C":
				return STATIONARY;
			case "N":
			case "NE":
			case "NW":
				return FORWARDS;
			case "S":
			case "SE":
			case "SW":
				return BACKWARDS;
			case "E":
				return RIGHT;
			case "W":
				return LEFT;
			default:
				return STATIONARY;
		}
	}
	
}
