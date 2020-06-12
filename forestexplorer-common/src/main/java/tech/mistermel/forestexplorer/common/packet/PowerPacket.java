package tech.mistermel.forestexplorer.common.packet;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

public class PowerPacket implements Packet {

	private float voltage;
	private float current;
	
	@SuppressWarnings("unused")
	private PowerPacket() {}
	
	public PowerPacket(float voltage, float current) {
		this.voltage = voltage;
		this.current = current;
	}
	
	@Override
	public void write(NetOutput out) throws IOException {
		out.writeFloat(voltage);
		out.writeFloat(current);
	}
	
	@Override
	public void read(NetInput in) throws IOException {
		this.voltage = in.readFloat();
		this.current = in.readFloat();
	}
	
	@Override
	public boolean isPriority() {
		return false;
	}
	
	public float getVoltage() {
		return voltage;
	}
	
	public float getCurrent() {
		return current;
	}
	
}
