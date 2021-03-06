package tech.mistermel.forestexplorer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.mistermel.forestexplorer.controller.ControllerHandler;
import tech.mistermel.forestexplorer.network.CommunicationHandler;

public class Robot {

	private static final Logger logger = LoggerFactory.getLogger(Robot.class);
	
	private CommunicationHandler communication;
	private ControllerHandler controller;
	
	public Robot() {
		this.communication = new CommunicationHandler();
		this.controller = new ControllerHandler();
	}
	
	public void start() {
		this.reconnect(0);
		controller.start();
	}
	
	public void reconnect(int delay) {
		try {
			Thread.sleep(delay * 1000);
		} catch (InterruptedException e) {}
		
		logger.info("Connecting...");
		while(!communication.connect()) {
			logger.warn("Could not connect, retrying in 20 seconds");
			
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {}
		}
		logger.info("Connected!");
	}
	
	public CommunicationHandler getCommunicationHandler() {
		return communication;
	}
	
	public ControllerHandler getControllerHandler() {
		return controller;
	}
	
}
