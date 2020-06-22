package tech.mistermel.forestexplorer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.mistermel.forestexplorer.common.MovementDirection;
import tech.mistermel.forestexplorer.common.NavigationMode;
import tech.mistermel.forestexplorer.common.Waypoint;
import tech.mistermel.forestexplorer.common.Waypoint.LatLng;
import tech.mistermel.forestexplorer.util.NavigationUtil;

public class Navigator {

	private static final double MAX_BEARING_OFFSET = 5;
	private static Logger logger = LoggerFactory.getLogger(Navigator.class);
	
	private List<Waypoint> waypoints = new ArrayList<Waypoint>();
	private Waypoint currentWaypoint;
	private int waypointIndex;
	
	private double targetBearing;
	private boolean bearingAligned;
	
	private MovementDirection currentMovement;
	
	public boolean startNavigation() {
		if(waypoints.size() == 0) {
			logger.warn("No waypoints loaded, unable to start waypoint navigation");
			return false;
		}
		
		this.currentMovement = MovementDirection.STATIONARY;
		this.setWaypoint(0);
		return true;
	}
	
	public void onLocationUpdate(LatLng loc) {
		this.targetBearing = NavigationUtil.getBearing(loc, currentWaypoint);
		
		double distance = NavigationUtil.getDistance(loc, currentWaypoint);
		if(distance < currentWaypoint.getRadius()) {
			int newWaypoint = waypointIndex + 1;
			if(newWaypoint > (waypoints.size() - 1)) {
				logger.info("Final waypoint reached, switching to manual control");
				setMovement(MovementDirection.STATIONARY);
				Launcher.instance().setNavigationMode(NavigationMode.MANUAL);
				return;
			}
			
			setWaypoint(newWaypoint);
		}
		
		if(bearingAligned) {
			setMovement(MovementDirection.FORWARDS);
		}
	}
	
	public void onCompassUpdate(double bearing) {
		double offset = Math.abs(bearing - targetBearing);
		if(offset > MAX_BEARING_OFFSET) {
			this.bearingAligned = false;
			
			// TODO: Make this take the fastest 'route' to the target bearing instead of just turning left
			setMovement(MovementDirection.LEFT);
		} else {
			this.bearingAligned = true;
		}
	}
	
	private void setMovement(MovementDirection dir) {
		if(this.currentMovement == dir) {
			return;
		}
		
		this.currentMovement = dir;
		Launcher.instance().getControllerHandler().setMovement(dir);
	}
	
	public void setWaypoint(int waypointIndex) {
		if(waypointIndex > (waypoints.size() - 1)) {
			throw new IllegalArgumentException("Waypoint index cannot exceed waypoint list size");
		}
		
		this.waypointIndex = waypointIndex;
		this.currentWaypoint = waypoints.get(waypointIndex);
		this.bearingAligned = false;
		
		logger.info("Now targeting waypoint #{}", waypointIndex);
	}
	
}
