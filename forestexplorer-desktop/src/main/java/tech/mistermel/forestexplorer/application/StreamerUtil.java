package tech.mistermel.forestexplorer.application;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamerUtil {

	private StreamerUtil() {}
	
	private static final Logger logger = LoggerFactory.getLogger(StreamerUtil.class);
	private static final String CMD = "gst-launch-1.0 -e udpsrc port=5001 ! application/x-rtp, encoding-name=JPEG,payload=26 ! rtpjpegdepay ! jpegdec ! autovideosink";
	
	private static Process process;
	
	public static void showStream() {
		if(isStreamActive())
			return;
		
		logger.info("Starting stream window");

		try {
			process = new ProcessBuilder(new String[] {"bash", "-c", CMD})
					.inheritIO()
					.start();
		} catch (IOException e) {
			logger.error("Error while starting streaming screen", e);
		}
	}
	
	public static void hideStream() {
		if(!isStreamActive())
			return;
		
		logger.info("Exiting stream window");
		try {
			process.destroyForcibly().waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isStreamActive() {
		return process != null && process.isAlive();
	}
	
}
