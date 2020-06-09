package tech.mistermel.forestexplorer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.mistermel.forestexplorer.util.PropertyFile;

public class Launcher {

	private static final Logger logger = LoggerFactory.getLogger(Launcher.class);
	private static Robot instance;
	
	public static void main(String[] args) {
		try {
			if(!PropertyFile.initialize()) {
				return;
			}
		} catch (IOException e) {
			logger.error("Error while intializing property file", e);
			return;
		}
		
		instance = new Robot();
		instance.start();
	}
	
	public static Robot instance() {
		return instance;
	}

}
