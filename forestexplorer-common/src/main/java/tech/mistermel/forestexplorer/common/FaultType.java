package tech.mistermel.forestexplorer.common;

public enum FaultType {

	CONTROLLER_COMMUNICATION("Controller communication failed"),
	BATTERY_TOO_LOW("Battery too low"),
	CURRENT_SENSOR("Current sensor failure");
	
	private String displayName;
	
	private FaultType(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
}
