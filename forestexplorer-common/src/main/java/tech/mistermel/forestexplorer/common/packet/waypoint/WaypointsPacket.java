package tech.mistermel.forestexplorer.common.packet.waypoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

import tech.mistermel.forestexplorer.common.Waypoint;
import tech.mistermel.forestexplorer.common.Waypoint.LatLng;

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
			LatLng location = waypoint.getLocation();
			out.writeDouble(location.getLatitude());
			out.writeDouble(location.getLongitude());
			out.writeDouble(waypoint.getRadius());
		}
	}
	
	@Override
	public void read(NetInput in) throws IOException {
		int length = in.readInt();
		for(int i = 0; i < length; i++) {
			Waypoint waypoint = new Waypoint(new LatLng(in.readDouble(), in.readDouble()), in.readDouble());
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
