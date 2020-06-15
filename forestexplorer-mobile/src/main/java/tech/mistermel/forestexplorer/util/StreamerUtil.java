package tech.mistermel.forestexplorer.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamerUtil {

	private StreamerUtil() {}
	
	private static final Logger logger = LoggerFactory.getLogger(StreamerUtil.class);
	private static final String CMD = "gst-launch-1.0 v4l2src device=/dev/video0 ! \"image/jpeg, width=1280, height=720, framerate=15/1\" ! rtpjpegpay ! udpsink host={{IP}} port=5001";
	
	private static Process process;
	
	public static void startStreamer() {
		if(isStreamerActive())
			return;
		
		logger.info("Starting streamer");

		try {
			process = new ProcessBuilder(new String[] {"bash", "-c", CMD.replace("{{IP}}", PropertyFile.getIP())})
					.inheritIO()
					.start();
		} catch (IOException e) {
			logger.error("Error while starting streaming screen", e);
		}
	}
	
	public static void stopStreamer() {
		if(!isStreamerActive())
			return;
		
		logger.info("Exiting streamer");
		process.destroy();
	}
	
	public static boolean isStreamerActive() {
		return process != null && process.isAlive();
	}
	
}
