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
	
	/*
	This snippet was taken from Apache Lucene's SloppyMath class
	License: https://github.com/apache/lucene-solr/blob/master/LICENSE
	*/
	
	private static final double TO_METERS = 6_371_008.7714D;
	
	public static double getDistance(LatLng loc, Waypoint dstWaypoint) {
		LatLng dst = dstWaypoint.getLocation();
		
		double x1 = Math.toRadians(loc.getLatitude());
		double x2 = Math.toRadians(dst.getLatitude());
		double h1 = 1 - Math.cos(x1 - x2);
		double h2 = 1 - Math.cos(Math.toRadians(loc.getLongitude() - dst.getLongitude()));
		double h = h1 + Math.cos(x1) * Math.cos(x2) * h2;
		double sortKey = Double.longBitsToDouble(Double.doubleToRawLongBits(h) & 0xFFFFFFFFFFFFFFF8L);
		
		return TO_METERS * 2 * Math.asin(Math.min(1, Math.sqrt(sortKey * 0.5)));
	}
	
}
