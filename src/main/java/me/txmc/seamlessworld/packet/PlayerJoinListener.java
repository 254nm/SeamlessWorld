package me.txmc.seamlessworld.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoinListener implements Listener {
    private final Plugin plugin;
    private final PacketEventDispatcher dispatcher;

    public PlayerJoinListener(Plugin plugin, PacketEventDispatcher dispatcher) {
        this.plugin = plugin;
        this.dispatcher = dispatcher;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        EntityPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();
        ChannelPipeline pipeline = player.playerConnection.networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", String.format("packet_listener%s", plugin.getName()), new ChannelInjector(dispatcher, event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        EntityPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();
        Channel channel = player.playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> channel.pipeline().remove(String.format("packet_listener%s", plugin.getName())));
    }

    @EventHandler
    public void onKick(PlayerQuitEvent event) {
        EntityPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();
        ChannelPipeline pipeline = player.playerConnection.networkManager.channel.pipeline();
        pipeline.remove(String.format("packet_listener%s", plugin.getName()));
    }
}
