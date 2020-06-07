package tech.mistermel.forestexplorer.common.packet;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

public class KeepAlivePacket implements Packet {

	private long pingTime;
	
	@SuppressWarnings("unused")
	private KeepAlivePacket() {}
	
	public KeepAlivePacket(long pingTime) {
		this.pingTime = pingTime;
	}
	
	@Override
	public void write(NetOutput out) throws IOException {
		out.writeLong(pingTime);
	}
	
	@Override
	public void read(NetInput in) throws IOException {
		this.pingTime = in.readLong();
	}
	
	@Override
	public boolean isPriority() {
		return true;
	}
	
	public long getPingTime() {
		return pingTime;
	}
	
}
