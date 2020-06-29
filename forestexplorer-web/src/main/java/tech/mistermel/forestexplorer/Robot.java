package tech.mistermel.forestexplorer;

import tech.mistermel.forestexplorer.controller.ControllerHandler;
import tech.mistermel.forestexplorer.web.WebHandler;

public class Robot {

	private WebHandler webHandler;
	private ControllerHandler controllerHandler;
	
	public Robot() {
		this.controllerHandler = new ControllerHandler();
		controllerHandler.start();
		
		this.webHandler = new WebHandler();
		webHandler.startServer();
	}
	
	public WebHandler getWebHandler() {
		return webHandler;
	}
	
}
