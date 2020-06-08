package tech.mistermel.forestexplorer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamerUtil {

	private StreamerUtil() {}
	
	private static final Logger logger = LoggerFactory.getLogger(StreamerUtil.class);
	private static final String CMD = "gst-launch-1.0 -v v4l2src device=/dev/video0 ! \"image/jpeg, width=1280, height=720, framerate=15/1\" ! rtpjpegpay ! udpsink host=192.168.178.3 port=5001";
	
	private static Process process;
	
	public static void startStreamer() {
		logger.warn("startStreamer() not implemented");
		/*if(process != null)
			return;
		
		logger.info("Starting streamer");
		ProcessBuilder pb = new ProcessBuilder(CMD.split(" ")).inheritIO();
		
		try {
			process = pb.start();
		} catch (IOException e) {
			logger.error("Error while starting streaming screen", e);
		}*/
	}
	
	public static void stopStreamer() {
		logger.warn("stopStreamer() not implemented");
		/*if(process == null)
			return;
		
		logger.info("Exiting streamer");
		process.destroy();*/
	}
	
	public static boolean isStreamerActive() {
		return process != null;
	}
	
}
