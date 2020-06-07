package tech.mistermel.forestexplorer.common.packet;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

public class VoltagePacket implements Packet {

	private float voltage;
	
	@SuppressWarnings("unused")
	private VoltagePacket() {}
	
	public VoltagePacket(float voltage) {
		this.voltage = voltage;
	}
	
	@Override
	public void write(NetOutput out) throws IOException {
		out.writeFloat(voltage);
	}
	
	@Override
	public void read(NetInput in) throws IOException {
		this.voltage = in.readFloat();
	}
	
	@Override
	public boolean isPriority() {
		return false;
	}
	
	public float getVoltage() {
		return voltage;
	}
	
}
