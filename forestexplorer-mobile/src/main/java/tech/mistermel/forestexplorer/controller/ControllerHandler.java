package tech.mistermel.forestexplorer.controller;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

import tech.mistermel.forestexplorer.Launcher;
import tech.mistermel.forestexplorer.common.CameraMovementDirection;
import tech.mistermel.forestexplorer.common.FaultType;
import tech.mistermel.forestexplorer.common.MovementDirection;
import tech.mistermel.forestexplorer.common.NavigationMode;
import tech.mistermel.forestexplorer.common.Waypoint.LatLng;

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
		logger.info("Available ports:");
		for(SerialPort port : ports) {
			logger.info("* {}: {}", port.getSystemPortName(), port.getPortDescription());
		}
		
		this.serial = selectPort(ports);
		logger.info("Using {}", serial.getSystemPortName());
		
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
	
	private void setFault(boolean fault) {
		if(this.fault == fault)
			return;
		this.fault = fault;
		Launcher.instance().getCommunicationHandler().setFault(FaultType.CONTROLLER, fault);
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
			
			if(msg.equals("p")) {
				sendCommand("p");
				return;
			}
			
			logger.debug("Controller message recieved: {}", msg);
			
			if(msg.equals("RST")) {
				reset();
				logger.warn("Controller reset (timeout?)");
				setFault(true);
				return;
			}
			
			if(msg.equals("lf")) {
				Launcher.instance().getCommunicationHandler().setFault(FaultType.GPS, true);
				return;
			}
			
			if(msg.startsWith("l")) {
				String[] args = msg.substring(1).split("/");
				
				LatLng loc = new LatLng(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
				int satteliteNum = Integer.parseInt(args[2]);
				
				if(Launcher.instance().getNavigationMode() == NavigationMode.WAYPOINT) {
					Launcher.instance().getNavigator().onLocationUpdate(loc);
				}
				
				Launcher.instance().getCommunicationHandler().sendLocation(loc, satteliteNum);
				return;
			}
			
			if(msg.equals("cf")) {
				Launcher.instance().getCommunicationHandler().setFault(FaultType.COMPASS, true);
				return;
			}
			
			if(msg.startsWith("C")) {
				double bearing = Double.parseDouble(msg.substring(1));
				
				if(Launcher.instance().getNavigationMode() == NavigationMode.WAYPOINT) {
					Launcher.instance().getNavigator().onCompassUpdate(bearing);
				}
				
				Launcher.instance().getCommunicationHandler().sendCompass(bearing);
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
	
	public void setCameraMovement(CameraMovementDirection movement) {
		if(!handshakeCompleted) {
			logger.debug("Did not send camera movement, handshake not complete");
			return;
		}
		
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
