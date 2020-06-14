package tech.mistermel.forestexplorer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyFile {

	private PropertyFile() {}

	private static final File FILE = new File("config.properties");
	private static final Logger logger = LoggerFactory.getLogger(PropertyFile.class);
	
	private static String ip;
	private static float minVoltage;
	
	public static boolean initialize() throws IOException {
		if(!FILE.exists())
			FILE.createNewFile();
		
		Properties properties = new Properties();
		properties.load(new FileInputStream(FILE));
		
		if(!properties.containsKey("ip")) {
			logger.error("config.properties must contain 'ip' key");
			return false;
		}
		ip = properties.getProperty("ip");
		
		if(!properties.containsKey("minVoltage")) {
			logger.error("config.properties must contain 'minVoltage' key");
			return false;
		}
		minVoltage = Float.parseFloat(properties.getProperty("minVoltage"));
		
		return true;
	}
	
	public static String getIP() {
		return ip;
	}
	
	public static float getMinVoltage() {
		return minVoltage;
	}
	
}
