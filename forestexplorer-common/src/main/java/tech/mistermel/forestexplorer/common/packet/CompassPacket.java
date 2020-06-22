package tech.mistermel.forestexplorer.common.packet;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

public class CompassPacket implements Packet {

	private double bearing;
	
	@SuppressWarnings("unused")
	private CompassPacket() {}
	
	public CompassPacket(double bearing) {
		this.bearing = bearing;
	}
	
	@Override
	public void write(NetOutput out) throws IOException {
		out.writeDouble(bearing);
	}
	
	@Override
	public void read(NetInput in) throws IOException {
		this.bearing = in.readDouble();
	}
	
	@Override
	public boolean isPriority() {
		return false;
	}
	
	public double getBearing() {
		return bearing;
	}
	
}
