package tech.mistermel.forestexplorer.application;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import tech.mistermel.forestexplorer.common.Waypoint.LatLng;

public class MapDisplay extends PApplet {

	private static final String USER_AGENT = "ForestExplorer";
	
	private UnfoldingMap map;
	
	private AbstractMarker currentLocationMarker;
	
	@Override
	public void setup() {
		System.setProperty("http.agent", USER_AGENT);
		
		surface.setTitle("Map");
		
		map = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());
	    MapUtils.createDefaultEventDispatcher(this, map);
	}
	
	public void updateLocation(LatLng loc) {
		Location mapLoc = this.getLocation(loc);
		if(currentLocationMarker == null) {
			currentLocationMarker = this.getDefaultMarker(mapLoc);
			
			map.addMarker(currentLocationMarker);
			map.zoomAndPanTo(15, mapLoc);
			return;
		}
		
		currentLocationMarker.setLocation(mapLoc);
	}
	
	private AbstractMarker getDefaultMarker(Location loc) {
		AbstractMarker marker = new DefaultMarker(loc);
		marker.setSelected(false);
		marker.setHidden(true);
		marker.setHighlightColor(color(0, 0, 0));
		marker.setHighlightStrokeColor(color(0, 0, 0));
		return marker;
	}
	
	public class DefaultMarker extends AbstractMarker {
		
		private int diameter = 15;
		
		public DefaultMarker(Location loc) {
			super(loc, null);
		}
		
		@Override
		public void draw(PGraphics pg, float x, float y) {
			/*
			pg.fill(0, 0, 255);
			pg.ellipse(x, y, map.getZoomLevel(), map.getZoomLevel());*/
		}
		
		@Override
		protected boolean isInside(float checkX, float checkY, float x, float y) {
			PVector pos = new PVector(x, y);
			return pos.dist(new PVector(checkX, checkY)) < diameter / 2;
		}
		
		public void setRadius(int radius) {
			this.diameter = radius * 2;
		}
		
		public void setDiameter(int diameter) {
			this.diameter = diameter;
		}
		
	}
	
	private Location getLocation(LatLng loc) {
		return new Location(loc.getLatitude(), loc.getLongitude());
	}
	
	@Override
	public void settings() {
	    size(800, 600, P2D);
	}
	
	@Override
	public void draw() {
		background(0);
	    map.draw();
	    
	    if(currentLocationMarker != null) {
	    	ScreenPosition pos = currentLocationMarker.getScreenPosition(map);
		    strokeWeight(16);
		    stroke(67, 211, 227, 100);
		    noFill();
		    ellipse(pos.x, pos.y, 36, 36);
	    }
	}
	
}
