package tech.mistermel.forestexplorer.common.packet;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

public class GPSPacket implements Packet {

	private float latitude, longitude;
	private int satteliteNum;
	
	@SuppressWarnings("unused")
	private GPSPacket() {}
	
	public GPSPacket(float latitude, float longitude, int satteliteNum) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.satteliteNum = satteliteNum;
	}
	
	@Override
	public void write(NetOutput out) throws IOException {
		out.writeFloat(latitude);
		out.writeFloat(longitude);
		out.writeInt(satteliteNum);
	}
	
	@Override
	public void read(NetInput in) throws IOException {
		this.latitude = in.readFloat();
		this.longitude = in.readFloat();
		this.satteliteNum = in.readInt();
	}
	
	@Override
	public boolean isPriority() {
		return false;
	}
	
	public float getLatitude() {
		return latitude;
	}
	
	public float getLongitude() {
		return longitude;
	}
	
	public int getSatteliteNum() {
		return satteliteNum;
	}
	
}
