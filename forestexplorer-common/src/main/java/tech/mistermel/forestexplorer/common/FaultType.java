package tech.mistermel.forestexplorer.common;

public enum FaultType {

	BATTERY_TOO_LOW("Battery too low"),
	CONTROLLER("Controller fault"),
	CURRENT_SENSOR("Current sensor fault"),
	GPS("GPS fault"),
	COMPASS("Compass fault");
	
	private String displayName;
	
	private FaultType(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
}
