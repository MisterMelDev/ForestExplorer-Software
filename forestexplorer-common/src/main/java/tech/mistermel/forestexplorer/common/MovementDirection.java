package tech.mistermel.forestexplorer.common;

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
	
	public static boolean isForwards(MovementDirection dir) {
		return dir == FORWARDS || dir == FORWARDS_LEFT || dir == FORWARDS_RIGHT;
	}
	
	public static boolean isBackwards(MovementDirection dir) {
		return dir == BACKWARDS || dir == BACKWARDS_LEFT || dir == BACKWARDS_RIGHT;
	}
	
	public static boolean isLeft(MovementDirection dir) {
		return dir == FORWARDS_LEFT || dir == BACKWARDS_LEFT || dir == LEFT;
	}
	
	public static boolean isRight(MovementDirection dir) {
		return dir == FORWARDS_RIGHT || dir == BACKWARDS_RIGHT || dir == RIGHT;
	}
	
	public int getLeftMotorDir() {
		return leftMotorDir;
	}
	
	public int getRightMotorDir() {
		return rightMotorDir;
	}
	
}
