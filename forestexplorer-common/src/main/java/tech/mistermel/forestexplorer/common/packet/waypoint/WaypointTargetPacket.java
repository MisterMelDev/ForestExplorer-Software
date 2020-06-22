package tech.mistermel.forestexplorer.common.packet.waypoint;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

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

