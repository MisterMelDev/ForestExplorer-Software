package tech.mistermel.forestexplorer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

import tech.mistermel.forestexplorer.Launcher;
import tech.mistermel.forestexplorer.common.CameraMovementDirection;
import tech.mistermel.forestexplorer.common.FaultType;
import tech.mistermel.forestexplorer.common.MovementDirection;

public class ControllerHandler extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(ControllerHandler.class);
	private static final int BAUDRATE = 115200;
	
	private SerialPort serial;
	private boolean handshakeCompleted;
	
	private boolean headlightsEnabled;
	private boolean warningLightsEnabled;
	private int brightness;
	
	private boolean fault;
	
	public ControllerHandler() {
		super("ControllerHandlerThread");
	}
	
	@Override
	public void run() {
		SerialPort[] ports = SerialPort.getCommPorts();
		this.serial = ports[ports.length - 1];
		logger.info("Using port {}", serial.getSystemPortName());
		
		if(serial.isOpen()) {
			logger.warn("Unable to open port, already open");
			setFault(true);
			return;
		}
		serial.setBaudRate(BAUDRATE);
		
		if(!serial.openPort()) {
			logger.warn("Unable to open port");
			setFault(true);
			return;
		}
		this.reset();
		
		serial.addDataListener(new MessageListener());
	}
	
	private void setFault(boolean fault) {
		if(this.fault == fault)
			return;
		this.fault = fault;
		Launcher.instance().getCommunicationHandler().setFault(FaultType.CONTROLLER_COMMUNICATION, fault);
	}
	
	public void safeState() {
		this.setMovement(MovementDirection.STATIONARY);
		this.setCameraMovement(CameraMovementDirection.STATIONARY);
		this.setLighting(false, false, 100);
	}
	
	private class MessageListener implements SerialPortMessageListener {

		@Override
		public void serialEvent(SerialPortEvent event) {
			String msg = new String(event.getReceivedData()).trim();
			
			if(!handshakeCompleted) {
				if(msg.equals("H")) {
					logger.info("Handshaked with controller");
					handshakeCompleted = true;
					sendCommand("H");
					setFault(false);
				}
				
				return;
			}
			
			if(msg.equals("RST")) {
				reset();
				logger.warn("Controller reset (timeout?)");
				setFault(true);
				return;
			}
			
			if(msg.equals("p")) {
				sendCommand("p");
				return;
			}
			
			if(msg.startsWith("l")) {
				String[] args = msg.substring(1).split("/");
				float latitude = Float.parseFloat(args[0]);
				float longitude = Float.parseFloat(args[1]);
				int satteliteNum = Integer.parseInt(args[2]);
				
				Launcher.instance().getCommunicationHandler().sendLocation(latitude, longitude, satteliteNum);
				return;
			}
		}
		
		@Override
		public int getListeningEvents() {
			return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
		}

		@Override
		public byte[] getMessageDelimiter() {
			return new byte[] {(byte) '\n'};
		}

		@Override
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
		
		cmd += "\n";
		byte[] buffer = cmd.getBytes();
		
		serial.writeBytes(buffer, buffer.length);
	}
	 
	public void setMovement(MovementDirection movement) {
		if(!handshakeCompleted)
			return;
		
		this.sendCommand("l" + movement.getLeftMotorDir());
		this.sendCommand("r" + movement.getRightMotorDir());
	}
	
	public void setCameraMovement(CameraMovementDirection movement) {
		if(!handshakeCompleted)
			return;
		
		this.sendCommand("c" + movement.getDirection());
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
