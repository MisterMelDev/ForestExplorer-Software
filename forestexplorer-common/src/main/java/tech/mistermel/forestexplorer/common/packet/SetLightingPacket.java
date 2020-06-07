package tech.mistermel.forestexplorer.common.packet;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

public class SetLightingPacket implements Packet {

	private boolean warningLightsEnabled;
	private boolean headlightsEnabled;
	private int brightness;

	@SuppressWarnings("unused")
	private SetLightingPacket() {}

	public SetLightingPacket(boolean warningLightsEnabled, boolean headlightsEnabled, int brightness) {
		this.warningLightsEnabled = warningLightsEnabled;
		this.headlightsEnabled = headlightsEnabled;
		this.brightness = brightness;
	}

	@Override
	public void write(NetOutput out) throws IOException {
		out.writeBoolean(warningLightsEnabled);
		out.writeBoolean(headlightsEnabled);
		out.writeInt(brightness);
	}

	@Override
	public void read(NetInput in) throws IOException {
		this.warningLightsEnabled = in.readBoolean();
		this.headlightsEnabled = in.readBoolean();
		this.brightness = in.readInt();
	}

	@Override
	public boolean isPriority() {
		return false;
	}

	public boolean isWarningLightsEnabled() {
		return warningLightsEnabled;
	}

	public boolean isHeadlightsEnabled() {
		return headlightsEnabled;
	}

	public int getBrightness() {
		return brightness;
	}

}
