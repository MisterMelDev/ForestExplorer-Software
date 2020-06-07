package tech.mistermel.forestexplorer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.mistermel.forestexplorer.controller.ControllerHandler;
import tech.mistermel.forestexplorer.network.CommunicationHandler;
import tech.mistermel.forestexplorer.util.PropertyFile;

public class Robot {

	private static final Logger logger = LoggerFactory.getLogger(Robot.class);
	
	private CommunicationHandler communication;
	private ControllerHandler controller;
	
	public Robot() {
		try {
			PropertyFile.initialize();
		} catch (IOException e) {
			logger.error("Error while intializing property file", e);
			return;
		}
		
		this.communication = new CommunicationHandler();
		this.controller = new ControllerHandler();
	}
	
	public void start() {
		while(!communication.connect()) {
			logger.warn("Could not connect, retrying in 20 seconds");
			
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {}
		}
		logger.info("Connected!");
		
		controller.start();
	}
	
	public CommunicationHandler getCommunicationHandler() {
		return communication;
	}
	
	public ControllerHandler getControllerHandler() {
		return controller;
	}
	
}
