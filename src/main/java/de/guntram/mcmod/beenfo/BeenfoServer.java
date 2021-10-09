/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.beenfo;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import static net.minecraft.state.property.Properties.HONEY_LEVEL;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/**
 *
 * @author gbl
 */
public class BeenfoServer implements ModInitializer {

    public static final Identifier C2SPacketIdentifier = new Identifier(Beenfo.MODID, "c2s");    
    public static final Identifier S2CPacketIdentifierOpen = new Identifier(Beenfo.MODID, "s2c");
    public static final Identifier S2CPacketIdentifierHud = new Identifier(Beenfo.MODID, "s2chud");

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(C2SPacketIdentifier, 
                (server, player, handler, buf, responseSender) -> {
                    processClientPacket(server, player, handler, buf, responseSender);
                });
    }

    /**
     * Send the info about a beehive that has been clicked on to the client.
     * This gets called from BeehiveBlockUseMixin.
     * @param player
     * @param honeyLevel
     * @param bees 
     */
    public static void sendBeehiveInfo(ServerPlayerEntity player, int honeyLevel, NbtList bees) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(honeyLevel);
        if (bees == null) {
            buf.writeInt(0);
        } else {
            buf.writeInt(bees.size());
            for (int i=0; i<bees.size(); i++) {
                NbtCompound tag = bees.getCompound(i).getCompound("EntityData");
                if (tag != null && tag.contains("CustomName", 8)) {
                    String beeName = tag.getString("CustomName");
                    buf.writeString(beeName);
                } else {
                    buf.writeString("");
                }
            }
        }
        ServerPlayNetworking.send(player, S2CPacketIdentifierOpen, buf);
    }
    
    /**
     * Process the packet which is sent by the client when looking at a hive.
     * @param server
     * @param player
     * @param handler
     * @param attachedData
     * @param responseSender 
     */

    private void processClientPacket(MinecraftServer server, ServerPlayerEntity player,
            ServerPlayNetworkHandler handler, PacketByteBuf attachedData, PacketSender responseSender) {
        int packetVersion = attachedData.readInt();
        BlockPos pos = attachedData.readBlockPos();
        server.execute(() -> {
            sendHudContent(player, pos, responseSender);
        });
    }

    private void sendHudContent(ServerPlayerEntity player, BlockPos pos, PacketSender responseSender) {
        BlockState state = player.world.getBlockState(pos);
        int honey = state.get(HONEY_LEVEL);
        BlockEntity entity = player.world.getBlockEntity(pos);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(0);        // packet version number
        buf.writeInt(honey);
        if (entity instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity bbe = (BeehiveBlockEntity) entity;
            NbtList tag = bbe.getBees();
            if (tag == null) {
                buf.writeInt(0);
            } else {
                buf.writeInt(tag.size());
            }
        } else {
            buf.writeInt(0);
        }
        buf.writeBlockPos(pos);
        responseSender.sendPacket(S2CPacketIdentifierHud, buf);
    }
}
