package tech.mistermel.forestexplorer.network;

import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.crypt.PacketEncryption;
import com.github.steveice10.packetlib.packet.DefaultPacketHeader;
import com.github.steveice10.packetlib.packet.PacketHeader;
import com.github.steveice10.packetlib.packet.PacketProtocol;

import tech.mistermel.forestexplorer.common.NetworkUtil;

public class ClientProtocol extends PacketProtocol {

	private final PacketHeader header = new DefaultPacketHeader();

	public ClientProtocol() {
		NetworkUtil.registerPackets(this);
	}
	
	@Override
	public PacketHeader getPacketHeader() {
		return header;
	}

	@Override
	public void newServerSession(Server server, Session session) {}
	
	@Override
	public void newClientSession(Client client, Session session) {}
	
	@Override
	public String getSRVRecordPrefix() {
		return null;
	}
	
	@Override
	public PacketEncryption getEncryption() {
		return null;
	}
	
}
