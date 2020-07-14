/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.beenfo;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author gbl
 */
public class BeenfoServer {

    // duplicate this here because we don't want to pull in Beenfo.class as 
    // that needs Screen which isn't present on dedi servers

    public static final ResourceLocation S2CPacketIdentifier = new ResourceLocation("beenfo", "s2c");

    public static void sendBeehiveInfo(PlayerEntity player, int honeyLevel, ListNBT bees) {
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        buf.writeInt(honeyLevel);
        if (bees == null) {
            buf.writeInt(0);
        } else {
            buf.writeInt(bees.size());
            for (int i=0; i<bees.size(); i++) {
                CompoundNBT tag = bees.getCompound(i).getCompound("EntityData");
                if (tag != null && tag.contains("CustomName", 8)) {
                    String beeName = tag.getString("CustomName");
                    buf.writeString(beeName);
                } else {
                    buf.writeString("");
                }
            }
        }
        ((ServerPlayerEntity)player).connection.getNetworkManager().sendPacket(
            NetworkDirection.PLAY_TO_CLIENT.buildPacket(Pair.of(buf, buf.writerIndex()), S2CPacketIdentifier).getThis()
        );
    }
}
