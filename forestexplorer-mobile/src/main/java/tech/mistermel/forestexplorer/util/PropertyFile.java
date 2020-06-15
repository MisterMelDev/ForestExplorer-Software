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
	private static int streamWidth, streamHeight, streamFps;
	
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
		
		if(!properties.containsKey("streamWidth")) {
			logger.error("config.properties must contain 'streamWidth' key");
			return false;
		}
		streamWidth = Integer.parseInt(properties.getProperty("streamWidth"));

		if(!properties.containsKey("streamHeight")) {
			logger.error("config.properties must contain 'streamHeight' key");
			return false;
		}
		streamHeight = Integer.parseInt(properties.getProperty("streamHeight"));
		
		if(!properties.containsKey("streamFps")) {
			logger.error("config.properties must contain 'streamFps' key");
			return false;
		}
		streamFps = Integer.parseInt(properties.getProperty("streamFps"));
		
		return true;
	}
	
	public static String getIP() {
		return ip;
	}
	
	public static float getMinVoltage() {
		return minVoltage;
	}
	
	public static int getStreamWidth() {
		return streamWidth;
	}
	
	public static int getStreamHeight() {
		return streamHeight;
	}
	
	public static int getStreamFps() {
		return streamFps;
	}
	
}
