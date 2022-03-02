package me.txmc.seamlessworld.packet;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_12_R1.Packet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChannelInjector extends ChannelDuplexHandler {
    private final PacketEventDispatcher dispatcher;
    private final Player player;

    public ChannelInjector(PacketEventDispatcher dispatcher, Player player) {
        this.dispatcher = dispatcher;
        this.player = player;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Bukkit.getServer().getScheduler().runTask(dispatcher.getPlugin(), () -> {
            PacketEvent.Outgoing outgoing = new PacketEvent.Outgoing((Packet<?>) msg, player);
            dispatcher.dispatch(outgoing);
            if (outgoing.isCancelled()) return;
            try {
                super.write(ctx, outgoing.getPacket(), promise);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Bukkit.getServer().getScheduler().runTask(dispatcher.getPlugin(), () -> {
            PacketEvent.Incoming incoming = new PacketEvent.Incoming((Packet<?>) msg, player);
            dispatcher.dispatch(incoming);
            if (incoming.isCancelled()) return;
            try {
                super.channelRead(ctx, incoming.getPacket());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
