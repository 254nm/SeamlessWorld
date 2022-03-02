package me.txmc.seamlessworld.packet;

public interface PacketListener {
    void incoming(PacketEvent.Incoming event);

    void outgoing(PacketEvent.Outgoing event);
}
