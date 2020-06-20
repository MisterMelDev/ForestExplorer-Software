package tech.mistermel.forestexplorer.common.packet.waypoint;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

/*
 * This packet can be both C->S and S->C.
 * 
 * If the client sends this packet to the server, it means
 * the previous waypoint was reached and it has adjusted its
 * target to the waypoint specified. (The next waypoint)
 * 
 * If the server sends this packet to the client, it means
 * the rover should adjust its target to the waypoint specified.
 * This is sent when the user selects a new waypoint in the GUI.
 */
public class WaypointTargetPacket implements Packet {

	private int newTarget;
	
	@SuppressWarnings("unused")
	private WaypointTargetPacket() {}
	
	public WaypointTargetPacket(int newTarget) {
		this.newTarget = newTarget;
	}
	
	@Override
	public void write(NetOutput out) throws IOException {
		out.writeInt(newTarget);
	}
	
	@Override
	public void read(NetInput in) throws IOException {
		this.newTarget = in.readInt();
	}
	
	@Override
	public boolean isPriority() {
		return false;
	}
	
	public int getNewTarget() {
		return newTarget;
	}
	
}

