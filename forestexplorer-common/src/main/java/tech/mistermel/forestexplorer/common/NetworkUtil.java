package tech.mistermel.forestexplorer.common;

import com.github.steveice10.packetlib.packet.PacketProtocol;

import tech.mistermel.forestexplorer.common.packet.CameraMovementPacket;
import tech.mistermel.forestexplorer.common.packet.CompassPacket;
import tech.mistermel.forestexplorer.common.packet.FaultPacket;
import tech.mistermel.forestexplorer.common.packet.GPSPacket;
import tech.mistermel.forestexplorer.common.packet.KeepAlivePacket;
import tech.mistermel.forestexplorer.common.packet.MovementPacket;
import tech.mistermel.forestexplorer.common.packet.SetLightingPacket;
import tech.mistermel.forestexplorer.common.packet.SetStreamingPacket;
import tech.mistermel.forestexplorer.common.packet.waypoint.NavigationModePacket;
import tech.mistermel.forestexplorer.common.packet.waypoint.NavigationUpdatePacket;
import tech.mistermel.forestexplorer.common.packet.waypoint.WaypointTargetPacket;
import tech.mistermel.forestexplorer.common.packet.waypoint.WaypointsPacket;
import tech.mistermel.forestexplorer.common.packet.PowerPacket;

public class NetworkUtil {

	private NetworkUtil() {}
	
	public static void registerPackets(PacketProtocol protocol) {
		protocol.register(0, MovementPacket.class);
		protocol.register(1, KeepAlivePacket.class);
		protocol.register(2, PowerPacket.class);
		protocol.register(3, SetLightingPacket.class);
		protocol.register(4, GPSPacket.class);
		protocol.register(5, CameraMovementPacket.class);
		protocol.register(6, SetStreamingPacket.class);
		protocol.register(7, FaultPacket.class);
		protocol.register(12, CompassPacket.class);
		
		protocol.register(8, NavigationModePacket.class);
		protocol.register(9, WaypointsPacket.class);
		protocol.register(10, WaypointTargetPacket.class);
		protocol.register(11, NavigationUpdatePacket.class);
	}
	
}
