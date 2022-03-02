package me.txmc.seamlessworld;

import me.txmc.seamlessworld.listener.MapChunkListener;
import me.txmc.seamlessworld.packet.PacketEventDispatcher;
import me.txmc.seamlessworld.packet.PacketListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class SeamlessWorld extends JavaPlugin {
    private PacketEventDispatcher packetEventBus;

    public static int getOppositeChunk(int x, int worldsize) {
        return ((x + worldsize) % (worldsize * 2)) - (worldsize * x / Math.abs(x));
    }

    @Override
    public void onEnable() {
        packetEventBus = new PacketEventDispatcher(this);
        register(new MapChunkListener(this));
    }

    public void register(Object o) {
        if (o instanceof PacketListener) {
            packetEventBus.register((PacketListener) o);
        } else if (o instanceof Listener) {
            getServer().getPluginManager().registerEvents((Listener) o, this);
        } else throw new IllegalArgumentException(String.format("Class %s is not a Listener", o.getClass().getName()));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
