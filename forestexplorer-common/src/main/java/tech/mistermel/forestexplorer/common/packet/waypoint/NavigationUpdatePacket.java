package tech.mistermel.forestexplorer.common.packet.waypoint;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

public class NavigationUpdatePacket implements Packet {

	private int targetedWaypoint;
	private double distance;
	
	@SuppressWarnings("unused")
	private NavigationUpdatePacket() {}
	
	public NavigationUpdatePacket(int targetedWaypoint, double distance) {
		this.targetedWaypoint = targetedWaypoint;
		this.distance = distance;
	}
	
	@Override
	public void write(NetOutput out) throws IOException {
		out.writeInt(targetedWaypoint);
		out.writeDouble(distance);
	}
	
	@Override
	public void read(NetInput in) throws IOException {
		this.targetedWaypoint = in.readInt();
		this.distance = in.readDouble();
	}
	
	@Override
	public boolean isPriority() {
		return false;
	}
	
	public int getTargetedWaypoint() {
		return targetedWaypoint;
	}
	
	public double getDistance() {
		return distance;
	}
	
}