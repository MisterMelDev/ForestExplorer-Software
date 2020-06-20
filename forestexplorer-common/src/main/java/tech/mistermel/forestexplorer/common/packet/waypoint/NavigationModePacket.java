package tech.mistermel.forestexplorer.common.packet.waypoint;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

import tech.mistermel.forestexplorer.common.NavigationMode;

public class NavigationModePacket implements Packet {

	private NavigationMode mode;
	
	@SuppressWarnings("unused")
	private NavigationModePacket() {}
	
	public NavigationModePacket(NavigationMode mode) {
		this.mode = mode;
	}
	
	@Override
	public void write(NetOutput out) throws IOException {
		out.writeString(mode.name());
	}
	
	@Override
	public void read(NetInput in) throws IOException {
		this.mode = NavigationMode.valueOf(in.readString());
	}
	
	@Override
	public boolean isPriority() {
		return true;
	}
	
	public NavigationMode getNavigationMode() {
		return mode;
	}
	
}
