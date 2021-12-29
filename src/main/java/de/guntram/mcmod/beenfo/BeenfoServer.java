/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.beenfo;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author gbl
 */
public class BeenfoServer {

    // duplicate this here because we don't want to pull in Beenfo.class as 
    // that needs Screen which isn't present on dedi servers

    public static final ResourceLocation S2CPacketIdentifier = new ResourceLocation("beenfo", "s2c");

    public static void sendBeehiveInfo(Player player, int honeyLevel, ListTag bees) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(honeyLevel);
        if (bees == null) {
            buf.writeInt(0);
        } else {
            buf.writeInt(bees.size());
            for (int i=0; i<bees.size(); i++) {
                CompoundTag tag = bees.getCompound(i).getCompound("EntityData");
                if (tag != null && tag.contains("CustomName", 8)) {
                    String beeName = tag.getString("CustomName");
                    buf.writeUtf(beeName);
                } else {
                    buf.writeUtf("");
                }
            }
        }
        ((ServerPlayer)player).connection.getConnection().send(
            NetworkDirection.PLAY_TO_CLIENT.buildPacket(Pair.of(buf, buf.writerIndex()), S2CPacketIdentifier).getThis()
        );
    }
}
