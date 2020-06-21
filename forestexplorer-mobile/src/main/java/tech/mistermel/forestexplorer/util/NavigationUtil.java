package tech.mistermel.forestexplorer.util;

import tech.mistermel.forestexplorer.common.Waypoint;
import tech.mistermel.forestexplorer.common.Waypoint.LatLng;

public class NavigationUtil {

	private NavigationUtil() {}
	
	public static double getBearing(LatLng src, Waypoint dstWaypoint) {
		LatLng dst = dstWaypoint.getLocation();
		
		double srcLat = Math.toRadians(src.getLatitude());
		double dstLat = Math.toRadians(dst.getLatitude());
		double dLng = Math.toRadians(dst.getLongitude() - src.getLongitude());
		
		double radians = Math.atan2(Math.sin(dLng) * Math.cos(dstLat), Math.cos(srcLat) * Math.sin(dstLat) - Math.sin(srcLat) * Math.cos(dstLat) * Math.cos(dLng));
		return Math.toDegrees((radians + Math.PI) % Math.PI);
	}
	
}
