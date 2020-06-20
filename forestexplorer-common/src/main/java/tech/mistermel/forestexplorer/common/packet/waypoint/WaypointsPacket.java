package tech.mistermel.forestexplorer.common.packet.waypoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

import tech.mistermel.forestexplorer.common.Waypoint;

public class WaypointsPacket implements Packet {

	private List<Waypoint> waypoints = new ArrayList<>();
	
	@SuppressWarnings("unused")
	private WaypointsPacket() {}
	
	public WaypointsPacket(List<Waypoint> waypoints) {
		this.waypoints = waypoints;
	}

	@Override
	public void write(NetOutput out) throws IOException {
		out.writeInt(waypoints.size());
		for(Waypoint waypoint : waypoints) {
			out.writeFloat(waypoint.getLatitude());
			out.writeFloat(waypoint.getLongitude());
			out.writeFloat(waypoint.getRadius());
		}
	}
	
	@Override
	public void read(NetInput in) throws IOException {
		int length = in.readInt();
		for(int i = 0; i < length; i++) {
			float latitude = in.readFloat();
			float longitude = in.readFloat();
			float radius = in.readFloat();
			
			Waypoint waypoint = new Waypoint(latitude, longitude, radius);
			waypoints.add(waypoint);
		}
	}

	@Override
	public boolean isPriority() {
		return false;
	}
	
	public List<Waypoint> getWaypoints() {
		return waypoints;
	}
	
}
