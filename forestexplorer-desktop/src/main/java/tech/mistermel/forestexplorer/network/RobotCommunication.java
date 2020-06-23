package tech.mistermel.forestexplorer.network;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

import tech.mistermel.forestexplorer.common.CameraMovementDirection;
import tech.mistermel.forestexplorer.common.FaultType;
import tech.mistermel.forestexplorer.common.MovementDirection;
import tech.mistermel.forestexplorer.common.Waypoint.LatLng;
import tech.mistermel.forestexplorer.common.packet.CameraMovementPacket;
import tech.mistermel.forestexplorer.common.packet.CompassPacket;
import tech.mistermel.forestexplorer.common.packet.FaultPacket;
import tech.mistermel.forestexplorer.common.packet.GPSPacket;
import tech.mistermel.forestexplorer.common.packet.KeepAlivePacket;
import tech.mistermel.forestexplorer.common.packet.MovementPacket;
import tech.mistermel.forestexplorer.common.packet.PowerPacket;
import tech.mistermel.forestexplorer.common.packet.SetLightingPacket;
import tech.mistermel.forestexplorer.common.packet.SetStreamingPacket;

public class RobotCommunication extends SessionAdapter {

	private static final int PORT = 8567;
	private static final Logger logger = LoggerFactory.getLogger(RobotCommunication.class);
	
	private Server server;
	
	private float voltage, current;
	private LatLng loc;
	private int satteliteNum;
	
	private MovementDirection movement = MovementDirection.STATIONARY;
	private short speedPercentage;
	
	private CameraMovementDirection cameraMovement = CameraMovementDirection.STATIONARY;
	
	private boolean warningLightsEnabled, headlightsEnabled;
	private short brightness = 100;
	
	private int pingMs;
	private long lastPingTime;
	
	private Set<FaultType> activeFaults = new HashSet<FaultType>();
	
	private double bearing;
	
	public RobotCommunication() {
		this.server = new Server("0.0.0.0", PORT, ServerProtocol.class, new TcpSessionFactory());
	}
	
	public void start() {
		logger.info("Starting server...");
		server.bind(true);
		new KeepAliveTask().start();
		logger.info("Started server on port {}", PORT);
	}
	
	@Override
	public void packetReceived(PacketReceivedEvent event) {
		Packet packet = event.getPacket();
		
		if(packet instanceof GPSPacket) {
			GPSPacket gpsPacket = (GPSPacket) packet;
			this.loc = gpsPacket.getLocation();
			this.satteliteNum = gpsPacket.getSatteliteNum();
			return;
		}
		
		if(packet instanceof PowerPacket) {
			PowerPacket powerPacket = (PowerPacket) packet;
			this.voltage = powerPacket.getVoltage();
			this.current = powerPacket.getCurrent();
			return;
		}
		
		if(packet instanceof FaultPacket) {
			FaultPacket faultPacket = (FaultPacket) packet;
			FaultType type = faultPacket.getType();
			
			if(faultPacket.isActive()) {
				activeFaults.add(type);
				logger.info("Fault reported: {}", type.name());
			} else {
				activeFaults.remove(type);
				logger.info("Fault cleared: {}", type.name());
			}
			return;
		}
		
		if(packet instanceof KeepAlivePacket) {
			KeepAlivePacket keepAlivePacket = (KeepAlivePacket) packet;
			if(keepAlivePacket.getPingTime() == lastPingTime) {
				this.pingMs = (int) (System.currentTimeMillis() - lastPingTime);
			}
			return;
		}
		
		if(packet instanceof CompassPacket) {
			this.bearing = ((CompassPacket) packet).getBearing();
			return;
		}
	}
	
	private class KeepAliveTask extends Thread {
		
		@Override
		public void run() {
			while(server.isListening()) {
				lastPingTime = System.currentTimeMillis();
				sendPacket(new KeepAlivePacket(lastPingTime));
				
				try {
					Thread.sleep(1000);
				} catch(InterruptedException e) {
					break;
				}
			}
			logger.debug("Exited keep alive task");
		}
		
	}
	
	public void sendPacket(Packet packet) {
		for(Session session : server.getSessions()) {
			session.send(packet);
		}
	}
	
	public void setSpeedPercentage(short speedPercentage) {
		this.speedPercentage = speedPercentage;
		/* The new speed percentage will be transmitted with the next movement packet */
	}
	
	public void sendMovement(MovementDirection movement) {
		if(this.movement == movement)
			return;
		
		this.movement = movement;
		MovementPacket packet = new MovementPacket(movement, speedPercentage);
		this.sendPacket(packet);
	}
	
	public void sendCameraMovement(CameraMovementDirection cameraMovement) {
		if(this.cameraMovement == cameraMovement)
			return;
		
		this.cameraMovement = cameraMovement;
		CameraMovementPacket packet = new CameraMovementPacket(cameraMovement);
		this.sendPacket(packet);
	}
	
	public void setWarningLightsEnabled(boolean warningLightsEnabled) {
		this.warningLightsEnabled = warningLightsEnabled;
		this.sendLightingPacket();
	}
	
	public void setHeadlightsEnabled(boolean headlightsEnabled) {
		this.headlightsEnabled = headlightsEnabled;
		this.sendLightingPacket();
	}
	
	public void setBrightness(short brightness) {
		this.brightness = brightness;
		
		if(!headlightsEnabled && !warningLightsEnabled) {
			/* If no lights are enabled, it is useless to send brightness.
			 * Brightness will be sent when lights status is changed.
			 */
			return;
		}
		
		this.sendLightingPacket();
	}
	
	private void sendLightingPacket() {
		if(brightness > 100) {
			logger.warn("Brightness cannot be higher than 100, setting to 100");
			brightness = 100;
		} else if(brightness < 0) {
			logger.warn("Brightness cannot be less than 0, setting to 0");
			brightness = 0;
		}
		
		SetLightingPacket packet = new SetLightingPacket(warningLightsEnabled, headlightsEnabled, brightness);
		this.sendPacket(packet);
	}
	
	public void setStreamingEnabled(boolean streamingEnabled) {
		SetStreamingPacket packet = new SetStreamingPacket(streamingEnabled);
		this.sendPacket(packet);
	}
	
	public float getVoltage() {
		return voltage;
	}
	
	public float getCurrent() {
		return current;
	}
	
	public LatLng getLocation() {
		return loc;
	}
	
	public int getSatteliteNum() {
		return satteliteNum;
	}
	
	public MovementDirection getMovement() {
		return movement;
	}
	
	public CameraMovementDirection getCameraMovement() {		
		return cameraMovement;
	}

	public short getSpeedPercentage() {
		return speedPercentage;
	}
	
	public boolean areWarningLightsEnabled() {
		return warningLightsEnabled;
	}
	
	public boolean areHeadlightsEnabled() {
		return headlightsEnabled;
	}
	
	public short getBrightness() {
		return brightness;
	}
	
	public Server getServer() {
		return server;
	}
	
	public int getConnectedCount() {
		return server.getSessions().size();
	}
	
	public int getPingMs() {
		return pingMs;
	}
	
	public Set<FaultType> getActiveFaults() {
		return activeFaults;
	}
	
	public double getBearing() {
		return bearing;
	}
	
}
