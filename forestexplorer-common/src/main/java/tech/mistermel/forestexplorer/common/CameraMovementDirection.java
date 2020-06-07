package tech.mistermel.forestexplorer.common;

public enum CameraMovementDirection {

	STATIONARY(0), RIGHT(1), LEFT(-1);
	
	private int dir;
	
	private CameraMovementDirection(int dir) {
		this.dir = dir;
	}
	
	public int getDirection() {
		return dir;
	}
	
}
