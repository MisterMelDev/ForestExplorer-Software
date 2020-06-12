package tech.mistermel.forestexplorer.application;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import processing.core.PApplet;
import tech.mistermel.forestexplorer.Launcher;
import tech.mistermel.forestexplorer.application.component.Checkbox;
import tech.mistermel.forestexplorer.application.component.ClickableComponent;
import tech.mistermel.forestexplorer.application.component.MovementVisualizer;
import tech.mistermel.forestexplorer.application.component.Slider;
import tech.mistermel.forestexplorer.common.CameraMovementDirection;
import tech.mistermel.forestexplorer.common.FaultType;
import tech.mistermel.forestexplorer.common.MovementDirection;
import tech.mistermel.forestexplorer.network.RobotCommunication;

public class InfoDisplay extends PApplet {

	private static final Logger logger = LoggerFactory.getLogger(InfoDisplay.class);

	private RobotCommunication robotComm;

	private Checkbox headlightsCheckbox, warningLightsCheckbox, transmitVideoCheckbox;
	private Slider brightnessSlider;
	private MovementVisualizer movementVisualizer;

	private Set<Integer> pressedKeys = new HashSet<>();
	private ClickableComponent draggingComponent;
	private int initStage;

	@Override
	public void setup() {
		surface.setTitle("ForestExplorer");

		this.headlightsCheckbox = new Checkbox(this, "Headlights", 10, 10);
		headlightsCheckbox.setCallback((checked) -> {
			robotComm.setHeadlightsEnabled(checked);
		});

		this.warningLightsCheckbox = new Checkbox(this, "Warning lights", 10, 30);
		warningLightsCheckbox.setCallback((checked) -> {
			robotComm.setWarningLightsEnabled(checked);
		});

		this.transmitVideoCheckbox = new Checkbox(this, "Transmit video", 10, 50);
		transmitVideoCheckbox.setCallback((checked) -> {
			robotComm.setStreamingEnabled(checked);
		});

		this.brightnessSlider = new Slider(this, "Lights Brightness", 200, 30, 100);
		brightnessSlider.setWaitForRelease(true);
		brightnessSlider.setCallback((percent) -> {
			robotComm.setBrightness(percent.shortValue());
		});

		this.movementVisualizer = new MovementVisualizer(this, width / 2, height / 2);
	}

	public void heavySetup() {
		this.robotComm = new RobotCommunication();
		robotComm.start();
	}

	@Override
	public void draw() {
		background(0);

		if (initStage == 0) {
			textSize(32);
			String text = "Loading";
			text(text, (width - textWidth(text)) / 2, height / 2);
			initStage = 1;
			return;
		}

		if (initStage == 1) {
			heavySetup();
			initStage = 2;

			long time = System.currentTimeMillis() - Launcher.startTime;
			float seconds = (float) time / 1000f;

			logger.info("Setup completed, took {}s", seconds);
		}

		headlightsCheckbox.draw();
		warningLightsCheckbox.draw();
		transmitVideoCheckbox.draw();
		brightnessSlider.draw();
		movementVisualizer.draw();

		int clientCount = robotComm.getConnectedCount();
		if(clientCount == 0) {
			String text = "Nothing connected";
			fill(255, 255, 0);
			text(text, (width - textWidth(text)) / 2, height - 90);
		} else {
			String text = (clientCount == 1 ? "1 device connected" : clientCount + "devices connected") + " (" + robotComm.getPingMs() + "ms)";
			fill(255, 255, 255);
			text(text, (width - textWidth(text)) / 2, height - 90);
		}

		fill(255, 0, 0);
		int yPos = height - 60;
		for (FaultType type : robotComm.getActiveFaults()) {
			text(type.getDisplayName(), (width - textWidth(type.getDisplayName())) / 2, yPos);
			yPos += 20;
		}
		
		textSize(13);
		fill(255, 255, 255);
		String text = robotComm.getVoltage() + "v " + robotComm.getCurrent() + "A";
		text(text, (width - textWidth(text)) / 2, 100);
	}

	@Override
	public void settings() {
		size(400, 400);
	}

	@Override
	public void mouseDragged() {
		if (draggingComponent != null) {
			draggingComponent.onClick();
			return;
		}

		if (brightnessSlider.intersects(mouseX, mouseY)) {
			draggingComponent = brightnessSlider;
			brightnessSlider.onClick();
		}
	}

	@Override
	public void mouseReleased() {
		draggingComponent = null;
		headlightsCheckbox.checkClick(mouseX, mouseY);
		warningLightsCheckbox.checkClick(mouseX, mouseY);
		transmitVideoCheckbox.checkClick(mouseX, mouseY);
	}

	@Override
	public void keyPressed() {
		if (!pressedKeys.contains(keyCode)) {
			pressedKeys.add(keyCode);
			this.calculateMovements();
		}
	}

	@Override
	public void keyReleased() {
		pressedKeys.remove(keyCode);
		this.calculateMovements();
	}

	public void calculateMovements() {
		int forward = 0;
		if (pressedKeys.contains(87)) {
			forward++;
		}
		if (pressedKeys.contains(83)) {
			forward--;
		}

		int sideways = 0;
		if (pressedKeys.contains(65)) {
			sideways++;
		}
		if (pressedKeys.contains(68)) {
			sideways--;
		}

		MovementDirection movement = MovementDirection.STATIONARY;
		if (forward == 1 && sideways == 0) {
			movement = MovementDirection.FORWARDS;
		} else if (forward == -1 && sideways == 0) {
			movement = MovementDirection.BACKWARDS;
		} else if (forward == 0 && sideways == 1) {
			movement = MovementDirection.LEFT;
		} else if (forward == 0 && sideways == -1) {
			movement = MovementDirection.RIGHT;
		} else if (forward == 1 && sideways == 1) {
			movement = MovementDirection.FORWARDS_LEFT;
		} else if (forward == 1 && sideways == -1) {
			movement = MovementDirection.FORWARDS_RIGHT;
		} else if (forward == -1 && sideways == 1) {
			movement = MovementDirection.BACKWARDS_LEFT;
		} else if (forward == -1 && sideways == -1) {
			movement = MovementDirection.BACKWARDS_RIGHT;
		}
		robotComm.sendMovement(movement);

		CameraMovementDirection cameraMovement = CameraMovementDirection.STATIONARY;
		if (pressedKeys.contains(81) && !pressedKeys.contains(69)) {
			cameraMovement = CameraMovementDirection.LEFT;
		} else if (pressedKeys.contains(69) && !pressedKeys.contains(81)) {
			cameraMovement = CameraMovementDirection.RIGHT;
		}
		robotComm.sendCameraMovement(cameraMovement);

		movementVisualizer.updateMovements(movement, cameraMovement);
	}

	public RobotCommunication getRobotComm() {
		return robotComm;
	}

}
