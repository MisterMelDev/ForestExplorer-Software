package tech.mistermel.forestexplorer.controller;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

public class ControllerHandler extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(ControllerHandler.class);
	private static final int BAUDRATE = 115200;
	
	private SerialPort serial;
	private boolean handshakeCompleted;
	
	private boolean headlightsEnabled;
	private boolean warningLightsEnabled;
	private int brightness;
	
	public ControllerHandler() {
		super("ControllerHandlerThread");
		this.selectPort();
	}
	
	private void selectPort() {
		SerialPort[] ports = SerialPort.getCommPorts();
		logger.info("Available ports:");
		for(SerialPort port : ports) {
			logger.info("* {}: {}", port.getSystemPortName(), port.getPortDescription());
		}
		
		this.serial = selectPort(ports);
		logger.info("Using port {}", serial.getSystemPortName());
	}
	
	@Override
	public void run() {
		if(serial.isOpen()) {
			logger.warn("Unable to open port, already open");
			return;
		}
		serial.setBaudRate(BAUDRATE);
		
		if(!serial.openPort()) {
			logger.warn("Unable to open port");
			return;
		}
		this.reset();
		serial.addDataListener(new MessageListener());
		logger.info("Controller ready, waiting for handshake");
	}
	
	private static List<String> PRIORITY_PORTS = Arrays.asList("ttyUSB0");
	
	private SerialPort selectPort(SerialPort[] ports) {
		for(SerialPort port : ports) {
			if(PRIORITY_PORTS.contains(port.getSystemPortName())) {
				logger.info("{} selected, is priority port", port.getSystemPortName());
				return port;
			}
		}
		
		return ports[ports.length - 1];
	}
	
	public void safeState() {
		this.setMovement(MovementDirection.STATIONARY);
		this.setLighting(false, false, 100);
	}
	
	private class MessageListener implements SerialPortMessageListener {
		
		public void serialEvent(SerialPortEvent event) {
			String msg = new String(event.getReceivedData()).trim();
			
			if(msg.startsWith("MSG")) {
				String systemMsg = msg.substring(3);
				logger.info("The controller sent the following message: {}", systemMsg.trim());
				return;
			}
			
			if(!handshakeCompleted) {
				if(msg.equals("H")) {
					logger.info("Handshaked with controller");
					handshakeCompleted = true;
					sendCommand("H");
				}
				
				return;
			}			
			
			if(msg.equals("p")) {
				sendCommand("p");
				return;
			}
			
			logger.debug("Controller message recieved: {}", msg);
			
			if(msg.equals("RST")) {
				reset();
				logger.warn("Controller reset (timeout?)");
				return;
			}
		}
		
		public int getListeningEvents() {
			return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
		}

		public byte[] getMessageDelimiter() {
			return new byte[] {(byte) '\n'};
		}

		public boolean delimiterIndicatesEndOfMessage() {
			return true;
		}
		
	}
	
	private void reset() {
		handshakeCompleted = false;
		headlightsEnabled = false;
		warningLightsEnabled = false;
		brightness = 100;
	}
	
	private void sendCommand(String cmd) {
		if(serial == null) {
			return;
		}
		
		if(!cmd.equals("p")) { // pings should not be debug logged
			logger.debug("Sending command: {}", cmd);
		}
		
		cmd += "\n";
		byte[] buffer = cmd.getBytes();
		
		serial.writeBytes(buffer, buffer.length);
	}
	 
	public void setMovement(MovementDirection movement) {
		if(!handshakeCompleted) {
			logger.debug("Did not send movement, handshake not complete");
			return;
		}
		
		this.sendCommand("l" + movement.getLeftMotorDir());
		this.sendCommand("r" + movement.getRightMotorDir());
	}
	
	public void setLighting(boolean headlightsEnabled, boolean warningLightsEnabled, int brightness) {
		if(!handshakeCompleted)
			return;
		
		if(headlightsEnabled != this.headlightsEnabled) {
			this.sendCommand("h" + (headlightsEnabled ? "1" : "0"));
			this.headlightsEnabled = headlightsEnabled;
		} else logger.debug("Ignoring headlights, already set");
		
		if(warningLightsEnabled != this.warningLightsEnabled) {
			this.sendCommand("w" + (warningLightsEnabled ? "1" : "0"));
			this.warningLightsEnabled = warningLightsEnabled;
		} else logger.debug("Ignoring warning lights, already set");
		
		if(brightness != this.brightness) {
			this.sendCommand("b" + brightness);
			this.brightness = brightness;
		} else logger.debug("Ignoring brightness, already set");
	}
	
}
