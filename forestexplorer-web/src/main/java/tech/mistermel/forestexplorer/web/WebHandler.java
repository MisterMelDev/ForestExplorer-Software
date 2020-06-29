package tech.mistermel.forestexplorer.web;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.iki.elonen.NanoWSD;
import fi.iki.elonen.NanoWSD.WebSocketFrame.CloseCode;
import tech.mistermel.forestexplorer.Launcher;
import tech.mistermel.forestexplorer.controller.MovementDirection;

public class WebHandler extends NanoWSD {

	private static Logger logger = LoggerFactory.getLogger(WebHandler.class);
	private static final int PORT = 8012;
	
	private WsdSocket socket;
	private FileHandler fileHandler;
	
	public WebHandler() {
		super(PORT);
		
		File webDir = new File("web");
		if(!webDir.isDirectory())
			webDir.mkdir();
		
		this.fileHandler = new FileHandler(webDir);
	}
	
	public void startServer() {
		try {
			this.start();
			logger.info("Webserver started on port {}", PORT);
		} catch (IOException e) {
			logger.error("An error occurred while starting the webserver", e);
		}
	}

	@Override
	public Response serve(IHTTPSession session) {
		if(session.getUri().equals("/ws")) {
			if(socket != null) {
				try {
					socket.close(CloseCode.GoingAway, "Connected from another location", false);
				} catch (IOException e) {
					logger.error("An error occurred while closing previous socket", e);
				}
			}
			
			return super.serve(session);
		}
		
		return fileHandler.serve(session.getUri());
	}
	
	@Override
	protected WebSocket openWebSocket(IHTTPSession handshake) {
		WsdSocket socket = new WsdSocket(handshake);
		this.socket = socket;
		return socket;
	}
	
	private class WsdSocket extends WebSocket {
		
		private final byte[] PING_PAYLOAD = "1337DEFDBEACR023".getBytes();
		
		private TimerTask pingTimer;
		private int ping, pong;
		
		public WsdSocket(IHTTPSession handshakeRequest) {
			super(handshakeRequest);
		}
		
		@Override
		protected void onMessage(WebSocketFrame message) {
			String text = message.getTextPayload();
			
			if(text.startsWith("joy")) {
				MovementDirection movement = MovementDirection.getFromWebName(text.substring(3));
				Launcher.instance.getControllerHandler().setMovement(movement);
				return;
			}
			
			if(text.startsWith("hl")) {
				boolean enabled = Boolean.parseBoolean(text.substring(2));
				Launcher.instance.getControllerHandler().setHeadlightsEnabled(enabled);
				return;
			}
			
			if(text.startsWith("wl")) {
				boolean enabled = Boolean.parseBoolean(text.substring(2));
				Launcher.instance.getControllerHandler().setWarningLightsEnabled(enabled);
				return;
			}
			
			if(text.startsWith("br")) {
				int value = Integer.parseInt(text.substring(2));
				Launcher.instance.getControllerHandler().setBrightness(value);
				return;
			}
		}
		
		@Override
		protected void onException(IOException exception) {
			logger.error("Error on websocket", exception);
		}

		@Override
		protected void onOpen() {
			logger.info("Websocket opened");
			pingTimer = new TimerTask() {
				@Override
				public void run() {
					try {
						ping(PING_PAYLOAD);
					} catch (IOException e) {
						logger.error("Error while attempting to ping", e);
						pingTimer.cancel();
					}
				}
			};
			new Timer().schedule(pingTimer, 1000, 1000);
		}

		@Override
		protected void onClose(CloseCode code, String reason, boolean initiatedByRemote) {
			logger.info("Websocket closed");
			
			if(pingTimer != null)
				pingTimer.cancel();
			
			Launcher.instance.getControllerHandler().safeState();
			socket = null;
		}
		
		@Override
		public void ping(byte[] payload) throws IOException {
			super.ping(payload);
			this.ping++;
			if(ping - pong > 3) close(CloseCode.GoingAway, "Missed too many ping requests", false);
		}

		@Override
		protected void onPong(WebSocketFrame pong) {
			this.pong++;
		}

	}

}
