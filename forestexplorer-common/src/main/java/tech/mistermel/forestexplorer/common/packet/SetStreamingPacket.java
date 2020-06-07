package tech.mistermel.forestexplorer.common.packet;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

public class SetStreamingPacket implements Packet {

	private boolean enabled;
	
	@SuppressWarnings("unused")
	private SetStreamingPacket() {}
	
	public SetStreamingPacket(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public void write(NetOutput out) throws IOException {
		out.writeBoolean(enabled);
	}
	
	@Override
	public void read(NetInput in) throws IOException {
		this.enabled = in.readBoolean();
	}

	@Override
	public boolean isPriority() {
		return false;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
}
