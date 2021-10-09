package de.guntram.bukkit.Beenfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityBeehive;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class Main extends JavaPlugin implements Listener, PluginMessageListener {
    
    public static final String MODID = "beenfo";
    public static final String C2SPacketIdentifier = MODID + ":" + "c2s";
    public static final String S2CPacketIdentifier = MODID + ":" + "s2c";
    public static final String S2CPacketIdentifierHud = MODID + ":" +"s2chud";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, S2CPacketIdentifier);
        getServer().getMessenger().registerOutgoingPluginChannel(this, S2CPacketIdentifierHud);
        getServer().getMessenger().registerIncomingPluginChannel(this, C2SPacketIdentifier, this);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        BlockState state;

        if (
                event.getAction() == Action.RIGHT_CLICK_BLOCK && 
                event.hasBlock() &&
                ((state=event.getClickedBlock().getState()) instanceof org.bukkit.block.Beehive) &&
                ( event.getItem() == null || event.getItem().getType() == Material.AIR)
        ) {
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

    @Override
    public void onPluginMessageReceived(String string, Player player, byte[] bytes) {
        try {
            DataInputStream is = new DataInputStream(new ByteArrayInputStream(bytes));
            int packetVersion = is.readInt();
            long blockPosLong = is.readLong();
            BlockPosition pos = BlockPosition.fromLong(blockPosLong);
            
            BlockData blockData = player.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getState().getBlockData();
            if (!(blockData instanceof Beehive hivedata)) {
                return;
            }
            TileEntityBeehive hive = (TileEntityBeehive) ((CraftWorld)player.getWorld()).getHandle().getTileEntity(pos);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(baos);
            os.writeInt(0);
            os.writeInt(hivedata.getHoneyLevel());
            os.writeInt(hive.getBeeCount());
            os.writeLong(blockPosLong);
            os.close();

            player.sendPluginMessage(this, S2CPacketIdentifierHud, baos.toByteArray());
            baos.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
