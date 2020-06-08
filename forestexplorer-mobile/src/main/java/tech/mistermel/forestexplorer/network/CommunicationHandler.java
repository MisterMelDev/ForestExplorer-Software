package tech.mistermel.forestexplorer.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

import tech.mistermel.forestexplorer.Launcher;
import tech.mistermel.forestexplorer.common.FaultType;
import tech.mistermel.forestexplorer.common.packet.CameraMovementPacket;
import tech.mistermel.forestexplorer.common.packet.FaultPacket;
import tech.mistermel.forestexplorer.common.packet.GPSPacket;
import tech.mistermel.forestexplorer.common.packet.KeepAlivePacket;
import tech.mistermel.forestexplorer.common.packet.MovementPacket;
import tech.mistermel.forestexplorer.common.packet.SetLightingPacket;
import tech.mistermel.forestexplorer.common.packet.SetStreamingPacket;
import tech.mistermel.forestexplorer.common.packet.VoltagePacket;
import tech.mistermel.forestexplorer.util.PropertyFile;
import tech.mistermel.forestexplorer.util.StreamerUtil;

public class CommunicationHandler extends SessionAdapter {

	private static final int PORT = 8567;
	private static final Logger logger = LoggerFactory.getLogger(CommunicationHandler.class);
	
	private Client client;
	
	public boolean connect() {
		this.client = new Client(PropertyFile.getIP(), PORT, new ClientProtocol(), new TcpSessionFactory());
		client.getSession().addListener(this);
		
		client.getSession().connect(true);
		return client.getSession().isConnected();
	}
	
	@Override
	public void packetReceived(PacketReceivedEvent event) {
		Packet packet = event.getPacket();
		
		if(packet instanceof KeepAlivePacket) {
			KeepAlivePacket keepAlivePacket = (KeepAlivePacket) packet;
			logger.debug("KeepAlivePacket (time: {})", keepAlivePacket.getPingTime());
			this.sendKeepAlive(keepAlivePacket.getPingTime());
			return;
		}
		
		if(packet instanceof MovementPacket) {
			MovementPacket movementPacket = (MovementPacket) packet;
			logger.debug("MovementPacket (movement: {})", movementPacket.getDirection().name());
			Launcher.instance().getControllerHandler().setMovement(movementPacket.getDirection());
			return;
		}
		
		if(packet instanceof CameraMovementPacket) {
			CameraMovementPacket cameraMovementPacket = (CameraMovementPacket) packet;
			logger.debug("CameraMovementPacket (movement: {})", cameraMovementPacket.getDirection().name());
			Launcher.instance().getControllerHandler().setCameraMovement(cameraMovementPacket.getDirection());
			return;
		}
		
		if(packet instanceof SetLightingPacket) {
			SetLightingPacket lightingPacket = (SetLightingPacket) packet;
			logger.debug("LightingPacket (headlights: {}, warninglights: {}, brightness: {})", lightingPacket.isHeadlightsEnabled(), lightingPacket.isWarningLightsEnabled(), lightingPacket.getBrightness());
			Launcher.instance().getControllerHandler().setLighting(lightingPacket.isHeadlightsEnabled(), lightingPacket.isWarningLightsEnabled(), lightingPacket.getBrightness());
			return;
		}
		
		if(packet instanceof SetStreamingPacket) {
			SetStreamingPacket streamingPacket = (SetStreamingPacket) packet;
			logger.debug("SetStreamingPacket (enabled: {})", streamingPacket.isEnabled());
			
			if(streamingPacket.isEnabled()) {
				StreamerUtil.startStreamer();
			} else {
				StreamerUtil.stopStreamer();
			}
			
			return;
		}
	}
	
	@Override
	public void disconnected(DisconnectedEvent event) {
		logger.warn("Disconnected from server, reason: {}", event.getReason());
		logger.error("Error", event.getCause());
		Launcher.instance().getControllerHandler().safeState();
	}
	
	public void setFault(FaultType type, boolean active) {
		FaultPacket packet = new FaultPacket(type, active);
		client.getSession().send(packet);
	}
	
	public void sendKeepAlive(long time) {
		KeepAlivePacket packet = new KeepAlivePacket(time);
		client.getSession().send(packet);
	}
	
	public void sendLocation(float latitude, float longitude, int satteliteNum) {
		GPSPacket packet = new GPSPacket(latitude, longitude, satteliteNum);
		client.getSession().send(packet);
		logger.info("New location: {} {} with {} sattelites", latitude, longitude, satteliteNum);
	}
	
	public void sendVoltage(float voltage) {
		VoltagePacket packet = new VoltagePacket(voltage);
		client.getSession().send(packet);
	}
	
	public Client getClient() {
		return client;
	}
}
