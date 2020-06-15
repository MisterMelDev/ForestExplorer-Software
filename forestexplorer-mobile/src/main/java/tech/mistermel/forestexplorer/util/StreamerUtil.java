package tech.mistermel.forestexplorer.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamerUtil {

	private StreamerUtil() {}
	
	private static final Logger logger = LoggerFactory.getLogger(StreamerUtil.class);
	private static final String CMD = "gst-launch-1.0 v4l2src device=/dev/video0 ! \"image/jpeg, width={{WIDTH}}, height={{HEIGHT}}, framerate={{FPS}}/1\" ! rtpjpegpay ! udpsink host={{IP}} port=5001";
	
	private static Process process;
	
	public static void startStreamer() {
		if(isStreamerActive())
			return;
		
		logger.info("Starting streamer");

		try {
			String cmd = CMD
					.replace("{{IP}}", PropertyFile.getIP())
					.replace("{{WIDTH}}", Integer.toString(PropertyFile.getStreamWidth()))
					.replace("{{HEIGHT}}", Integer.toString(PropertyFile.getStreamHeight()))
					.replace("{{FPS}}", Integer.toString(PropertyFile.getStreamFps()));
			
			process = new ProcessBuilder(new String[] {"bash", "-c", cmd})
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
