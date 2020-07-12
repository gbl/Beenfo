/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.beenfo;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

/**
 *
 * @author gbl
 */
public class BeenfoServer implements ModInitializer {

    private MinecraftServer server;

    @Override
    public void onInitialize() {
        // that doesn't exist yet on older api versions ...
        // ServerLifecycleEvents.SERVER_STARTED.register(this);
        ServerStartCallback.EVENT.register(this::onServerStarted);
    }

    // @Override
    public void onServerStarted(MinecraftServer server) {
        this.server = server;
    }
    
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
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Beenfo.S2CPacketIdentifier, buf);
    }
}
