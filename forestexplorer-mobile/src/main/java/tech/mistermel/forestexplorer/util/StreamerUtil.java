package tech.mistermel.forestexplorer.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamerUtil {

	private StreamerUtil() {}
	
	private static final Logger logger = LoggerFactory.getLogger(StreamerUtil.class);
	private static final String CMD = "screen -mdS streamer gst-launch-1.0 -v v4l2src device=/dev/video0 ! \"image/jpeg, width=1280, height=720, framerate=15/1\" ! rtpjpegpay ! udpsink host=192.168.178.3 port=5001";
	private static final String EXIT_CMD = "screen -S streamer -X quit";
	
	private static boolean isActive;
	
	public static void startStreamer() {
		logger.info("Starting streamer screen");
		ProcessBuilder pb = new ProcessBuilder(CMD.split(" "));
		
		try {
			pb.start();
			isActive = true;
		} catch (IOException e) {
			logger.error("Error while starting streaming screen", e);
		}
	}
	
	public static void stopStreamer() {
		logger.info("Exiting streamer screen");
		ProcessBuilder pb = new ProcessBuilder(EXIT_CMD.split(" "));
		
		try {
			pb.start();
			isActive = false;
		} catch (IOException e) {
			logger.error("Error while exiting streaming screen", e);
		}
	}
	
	public static boolean isStreamerActive() {
		return isActive;
	}
	
}
