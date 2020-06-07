package tech.mistermel.forestexplorer.common.packet;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

import tech.mistermel.forestexplorer.common.FaultType;

public class FaultPacket implements Packet {
	
	private FaultType type;
	private boolean active;
	
	@SuppressWarnings("unused")
	private FaultPacket() {}
	
	public FaultPacket(FaultType type, boolean active) {
		this.type = type;
		this.active = active;
	}

	@Override
	public void write(NetOutput out) throws IOException {
		out.writeInt(type.ordinal());
		out.writeBoolean(active);
	}

	@Override
	public void read(NetInput in) throws IOException {
		this.type = FaultType.values()[in.readInt()];
		this.active = in.readBoolean();
	}

	@Override
	public boolean isPriority() {
		return false;
	}
	
	public FaultType getType() {
		return type;
	}
	
	public boolean isActive() {
		return active;
	}

}
