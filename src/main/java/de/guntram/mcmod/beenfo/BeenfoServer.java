/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.beenfo;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import static net.minecraft.state.property.Properties.HONEY_LEVEL;
import net.minecraft.util.math.BlockPos;

/**
 *
 * @author gbl
 */
public class BeenfoServer implements ModInitializer {

    // duplicate this here because we don't want to pull in Beenfo.class as 
    // that needs Screen which isn't present on dedi servers

    public static void sendBeehiveInfo(PlayerEntity player, int honeyLevel, ListTag bees) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(honeyLevel);
        if (bees == null) {
            buf.writeInt(0);
        } else {
            buf.writeInt(bees.size());
            for (int i=0; i<bees.size(); i++) {
                CompoundTag tag = bees.getCompound(i).getCompound("EntityData");
                if (tag != null && tag.contains("CustomName", 8)) {
                    String beeName = tag.getString("CustomName");
                    buf.writeString(beeName);
                } else {
                    buf.writeString("");
                }
            }
        }
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Beenfo.S2CPacketIdentifierOpen, buf);
    }

    @Override
    public void onInitialize() {
        ServerSidePacketRegistry.INSTANCE.register(Beenfo.C2SPacketIdentifier,
                (packetContext, attachedData) -> {
                    processClientPacket(packetContext, attachedData);
                });
    }
    
    private void processClientPacket(PacketContext packetContext, PacketByteBuf attachedData) {
        int packetVersion = attachedData.readInt();
        BlockPos pos = attachedData.readBlockPos();
        packetContext.getTaskQueue().execute(() -> sendHudContent(packetContext.getPlayer(), pos));
    }

    private void sendHudContent(PlayerEntity player, BlockPos pos) {
        BlockState state = player.world.getBlockState(pos);
        int honey = state.get(HONEY_LEVEL);
        BlockEntity entity = player.world.getBlockEntity(pos);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(0);        // packet version number
        buf.writeInt(honey);
        if (entity instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity bbe = (BeehiveBlockEntity) entity;
            ListTag tag = bbe.getBees();
            if (tag == null) {
                buf.writeInt(0);
            } else {
                buf.writeInt(tag.size());
            }
        } else {
            buf.writeInt(0);
        }
        buf.writeBlockPos(pos);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Beenfo.S2CPacketIdentifierHud, buf);
    }
}
