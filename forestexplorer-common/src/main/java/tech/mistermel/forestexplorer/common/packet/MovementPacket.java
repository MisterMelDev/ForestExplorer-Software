package tech.mistermel.forestexplorer.common.packet;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

import tech.mistermel.forestexplorer.common.MovementDirection;

public class MovementPacket implements Packet {

	private MovementDirection direction;
	private short speedPercentage;
	
	@SuppressWarnings("unused")
	private MovementPacket() {}
	
	public MovementPacket(MovementDirection direction, short speedPercentage) {
		this.direction = direction;
		this.speedPercentage = speedPercentage;
	}
	
	@Override
	public void write(NetOutput out) throws IOException {
		out.writeInt(direction.ordinal());
		out.writeShort(speedPercentage);
	}
	
	@Override
	public void read(NetInput in) throws IOException {
		this.direction = MovementDirection.values()[in.readInt()];
		this.speedPercentage = in.readShort();
	}
	
	@Override
	public boolean isPriority() {
		return true;
	}
	
	public MovementDirection getDirection() {
		return direction;
	}
	
	public int getSpeedPercentage() {
		return speedPercentage;
	}
	
}
