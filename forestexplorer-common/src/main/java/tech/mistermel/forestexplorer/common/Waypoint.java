package tech.mistermel.forestexplorer.common;

public class Waypoint {

	private float latitude, longitude, radius;
	
	public Waypoint(float latitude, float longitude, float radius) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
	}
	
	public float getLatitude() {
		return latitude;
	}

	public float getLongitude() {
		return longitude;
	}
	
	public float getRadius() {
		return radius;
	}
	
}
