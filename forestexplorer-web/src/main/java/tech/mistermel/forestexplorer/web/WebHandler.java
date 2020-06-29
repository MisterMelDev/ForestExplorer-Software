package tech.mistermel.forestexplorer.web;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.iki.elonen.NanoWSD;
import fi.iki.elonen.NanoWSD.WebSocketFrame.CloseCode;

public class WebHandler extends NanoWSD {

	private static Logger logger = LoggerFactory.getLogger(WebHandler.class);
	private static final int PORT = 8012;
	
	private WsdSocket socket;
	
	public WebHandler() {
		super(PORT);
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
				return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "WebSocket already connected");
			}
			
			return super.serve(session);
		}
		
		return newFixedLengthResponse("yes hello");
	}
	
	@Override
	protected WebSocket openWebSocket(IHTTPSession handshake) {
		WsdSocket socket = new WsdSocket(handshake);
		this.socket = socket;
		return socket;
	}
	
	private static class WsdSocket extends WebSocket {
		
		public WsdSocket(IHTTPSession handshakeRequest) {
			super(handshakeRequest);
		}
		
		@Override
		protected void onMessage(WebSocketFrame message) {
			logger.info(message.getTextPayload());
		}
		
		@Override
		protected void onException(IOException exception) {
			logger.error("Error on websocket", exception);
		}

		@Override
		protected void onOpen() {
			logger.info("Websocket opened");
		}

		@Override
		protected void onClose(CloseCode code, String reason, boolean initiatedByRemote) {
			logger.info("Websocket closed");
		}

		@Override
		protected void onPong(WebSocketFrame pong) {
			System.out.println("PONG");
		}

	}

}
