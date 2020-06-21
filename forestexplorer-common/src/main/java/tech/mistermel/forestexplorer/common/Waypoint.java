package tech.mistermel.forestexplorer.common;

public class Waypoint {

	private LatLng location;
	private double radius;
	
	public Waypoint(LatLng location, double radius) {
		this.location = location;
		this.radius = radius;
	}
	
	public LatLng getLocation() {
		return location;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public static class LatLng {
		
		private double latitude, longitude;
		
		public LatLng(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}
		
		public double getLatitude() {
			return latitude;
		}
		
		public double getLongitude() {
			return longitude;
		}
		
	}
	
}
