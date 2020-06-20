package tech.mistermel.forestexplorer.application;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

public class MapDisplay extends PApplet {

	private static final String USER_AGENT = "ForestExplorer";
	
	private UnfoldingMap map;
	
	@Override
	public void setup() {
		System.setProperty("http.agent", USER_AGENT);
		
		map = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());
	    MapUtils.createDefaultEventDispatcher(this, map);
	}
	
	@Override
	public void settings() {
	    size(800, 600); 
	}
	
	@Override
	public void draw() {
		background(0);
	    map.draw();
	}
	
	public static void main(String[] args) {
		PApplet.runSketch(new String[] { "MapDisplay" }, new MapDisplay());
	}
	
}
