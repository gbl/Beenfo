package de.guntram.bukkit.Beenfo;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import net.minecraft.server.v1_16_R3.TileEntity;
import net.minecraft.server.v1_16_R3.TileEntityBeehive;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    
    public static final String MODID = "beenfo";
    public static final String S2CPacketIdentifier = MODID + ":" +"s2c";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, S2CPacketIdentifier);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        BlockState state;
/*
        System.out.println("action "+event.getAction());
        System.out.println("has block "+event.hasBlock());
        System.out.println("clicked Block "+event.getClickedBlock());
        System.out.println("Item "+event.getItem());
*/
        if (
                event.getAction() == Action.RIGHT_CLICK_BLOCK && 
                event.hasBlock() &&
                ((state=event.getClickedBlock().getState()) instanceof org.bukkit.block.Beehive) &&
                ( event.getItem() == null || event.getItem().getType() == Material.AIR)
        ) {
//            System.out.println("interact with hive!");
            Player player = event.getPlayer();
            Beehive hiveData = (Beehive) state.getBlockData();
            int honeyLevel = hiveData.getHoneyLevel();
            CraftWorld world = (CraftWorld)player.getWorld();
            Location l = event.getClickedBlock().getLocation();
            BlockPosition pos = new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ());
            
            TileEntity te = world.getHandle().getTileEntity(pos);
            if (te instanceof TileEntityBeehive) {
                TileEntityBeehive hive = (TileEntityBeehive) te;
                NBTTagCompound compound = new NBTTagCompound();
                hive.save(compound);
                NBTTagList bees = compound.getList("Bees", 10);
                int neededSize = 8;
                for (int i=0; i<bees.size(); i++) {
                    NBTTagCompound beeData = bees.getCompound(i).getCompound("EntityData");
                    if (beeData != null && beeData.hasKeyOfType("CustomName", 8)) {
                        String beeName = beeData.getString("CustomName");
                        neededSize += beeName.length() + 1;
                    } else {
                        neededSize++;
                    }
                }
                byte[] sendBuffer = new byte[neededSize];
                writeInt(sendBuffer, 0, honeyLevel);
                writeInt(sendBuffer, 4, bees.size());
                int bufPos=8;
                for (int i=0; i<bees.size(); i++) {
                    NBTTagCompound beeData = bees.getCompound(i).getCompound("EntityData");
                    if (beeData != null && beeData.hasKeyOfType("CustomName", 8)) {
                        String beeName = beeData.getString("CustomName");
                        sendBuffer[bufPos++] = (byte) beeName.length();
                        System.arraycopy(beeName.getBytes(), 0, sendBuffer, bufPos, beeName.length());
                    } else {
                        sendBuffer[bufPos++] = 0;
                    }
                }
                player.sendPluginMessage(this, S2CPacketIdentifier, sendBuffer);
            }
        }
    }
    
    // naively assume 0<=val<=127
    private void writeInt(byte[] buffer, int pos, int val) {
        buffer[pos] = buffer[pos+1] = buffer[pos+2] = 0;
        buffer[pos+3] = (byte)val;
    }
}
