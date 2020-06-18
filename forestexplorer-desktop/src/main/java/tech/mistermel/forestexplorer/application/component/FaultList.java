package tech.mistermel.forestexplorer.application.component;

import java.util.Set;

import tech.mistermel.forestexplorer.application.InfoDisplay;
import tech.mistermel.forestexplorer.common.FaultType;

public class FaultList implements Component {

	private InfoDisplay applet;
	private int x, y;
	
	public FaultList(InfoDisplay applet, int x, int y) {
		this.applet = applet;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void draw() {
		applet.textSize(13);
		
		this.x = 10;
		
		int currentY = y;
		
		applet.fill(255);
		applet.text("Faults:", x, currentY);
		
		Set<FaultType> activeFaults = applet.getRobotComm().getActiveFaults();
		if(activeFaults.size() == 0) {
			applet.fill(0, 255, 0);
			applet.text("No faults", x, currentY += 15);
		}
		
		applet.fill(255, 0, 0);
		for(FaultType fault : activeFaults) {
			applet.text(fault.getDisplayName(), x, currentY += 15);
		}
	}
	
}
