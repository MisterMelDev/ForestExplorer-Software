package tech.mistermel.forestexplorer.common.packet;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

import tech.mistermel.forestexplorer.common.Waypoint.LatLng;

public class GPSPacket implements Packet {

	private LatLng location;
	private int satteliteNum;
	
	@SuppressWarnings("unused")
	private GPSPacket() {}
	
	public GPSPacket(LatLng location, int satteliteNum) {
		this.location = location;
		this.satteliteNum = satteliteNum;
	}
	
	@Override
	public void write(NetOutput out) throws IOException {
		out.writeDouble(location.getLatitude());
		out.writeDouble(location.getLongitude());
		out.writeInt(satteliteNum);
	}
	
	@Override
	public void read(NetInput in) throws IOException {
		this.location = new LatLng(in.readDouble(), in.readDouble());
		this.satteliteNum = in.readInt();
	}
	
	@Override
	public boolean isPriority() {
		return false;
	}
	
	public LatLng getLocation() {
		return location;
	}
	
	public int getSatteliteNum() {
		return satteliteNum;
	}
	
}
