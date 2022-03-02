package me.txmc.seamlessworld.listener;

import me.txmc.seamlessworld.SeamlessWorld;
import me.txmc.seamlessworld.packet.PacketEvent;
import me.txmc.seamlessworld.packet.PacketListener;
import net.minecraft.server.v1_12_R1.PacketPlayOutMapChunk;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class MapChunkListener implements PacketListener {
    private final int border = 20; //In chunks
    private final SeamlessWorld plugin;
    private Field chunkXF;
    private Field chunkZF;

    public MapChunkListener(SeamlessWorld plugin) {
        this.plugin = plugin;
        try {
            chunkXF = PacketPlayOutMapChunk.class.getDeclaredField("a");
            chunkZF = PacketPlayOutMapChunk.class.getDeclaredField("b");
            chunkXF.setAccessible(true);
            chunkZF.setAccessible(true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void incoming(PacketEvent.Incoming event) {
    }

    @Override
    public void outgoing(PacketEvent.Outgoing event) {
        if (event.getPacket() instanceof PacketPlayOutMapChunk) {
            Player player = event.getPlayer();
            try {
                PacketPlayOutMapChunk packet = (PacketPlayOutMapChunk) event.getPacket();
                int chunkX = chunkXF.getInt(packet);
                int chunkZ = chunkZF.getInt(packet);
                if (chunkX > border || -chunkZ > border || -chunkX > border || chunkZ > border) {
                    int invertedX = SeamlessWorld.getOppositeChunk(chunkX, border), invertedZ = SeamlessWorld.getOppositeChunk(chunkZ, border);
                    System.out.printf("Original X: %d InvertedX %d, OriginalZ: %d InvertedZ: %d\n", chunkX, invertedX, chunkZ, invertedZ);
//                    event.setCancelled(true);
                    CraftChunk chunk = (CraftChunk) player.getWorld().getChunkAt(invertedX, invertedZ);
                    PacketPlayOutMapChunk mapChunk = new PacketPlayOutMapChunk(chunk.getHandle(), '\uffff');
                    chunkXF.set(mapChunk, chunkX);
                    chunkZF.set(mapChunk, chunkZ);
                    event.setPacket(mapChunk);
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
