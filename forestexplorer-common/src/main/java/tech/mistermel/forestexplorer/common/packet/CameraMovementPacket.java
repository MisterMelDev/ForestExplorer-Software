package tech.mistermel.forestexplorer.common.packet;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

import tech.mistermel.forestexplorer.common.CameraMovementDirection;

public class CameraMovementPacket implements Packet {

	private CameraMovementDirection direction;
	
	@SuppressWarnings("unused")
	private CameraMovementPacket() {}
	
	public CameraMovementPacket(CameraMovementDirection direction) {
		this.direction = direction;
	}
	
	@Override
	public void write(NetOutput out) throws IOException {
		out.writeInt(direction.ordinal());
	}

	@Override
	public void read(NetInput in) throws IOException {
		this.direction = CameraMovementDirection.values()[in.readInt()];
	}

	@Override
	public boolean isPriority() {
		return true;
	}
	
	public CameraMovementDirection getDirection() {
		return direction;
	}
	
}
