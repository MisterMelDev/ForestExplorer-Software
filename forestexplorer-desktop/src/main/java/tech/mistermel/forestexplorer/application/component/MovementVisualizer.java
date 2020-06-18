package tech.mistermel.forestexplorer.application.component;

import processing.core.PApplet;
import tech.mistermel.forestexplorer.common.CameraMovementDirection;
import tech.mistermel.forestexplorer.common.MovementDirection;

public class MovementVisualizer implements Component {

	private static final int GAP_MIDDLE = 25, LINE_LENGTH = 50, DIAGONAL_GAP = 35;
	
	private PApplet applet;
	private int x, y;
	
	private MovementDirection movement = MovementDirection.STATIONARY;
	private CameraMovementDirection cameraMovement = CameraMovementDirection.STATIONARY;
	
	public MovementVisualizer(PApplet applet, int x, int y) {
		this.applet = applet;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void draw() {
		applet.strokeWeight(8);
		
		int lineOffset = GAP_MIDDLE + LINE_LENGTH;
		drawLine(MovementDirection.isForwards(movement), x, y - GAP_MIDDLE, x, y - lineOffset);
		drawLine(MovementDirection.isLeft(movement), x - GAP_MIDDLE, y, x - lineOffset, y);
		drawLine(MovementDirection.isRight(movement), x + GAP_MIDDLE, y, x + lineOffset, y);
		drawLine(MovementDirection.isBackwards(movement), x, y + GAP_MIDDLE, x, y + lineOffset);
		
		drawLine(cameraMovement == CameraMovementDirection.LEFT, x - lineOffset, y - DIAGONAL_GAP, x - DIAGONAL_GAP, y - lineOffset);
		drawLine(cameraMovement == CameraMovementDirection.RIGHT, x + lineOffset, y - DIAGONAL_GAP, x + DIAGONAL_GAP, y - lineOffset);
		
		applet.noStroke();
	}
	
	public void updateMovements(MovementDirection movement, CameraMovementDirection cameraMovement) {
		this.movement = movement;
		this.cameraMovement = cameraMovement;
	}
	
	public void drawLine(boolean enabled, int x1, int y1, int x2, int y2) {
		applet.stroke(enabled ? 0 : 255, 255, enabled ? 0 : 255);
		applet.line(x1, y1, x2, y2);
	}
	
}
