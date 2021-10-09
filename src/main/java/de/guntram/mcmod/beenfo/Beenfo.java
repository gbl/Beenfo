package de.guntram.mcmod.beenfo;

import de.guntram.mcmod.beenfo.config.ConfigurationHandler;
import de.guntram.mcmod.crowdintranslate.CrowdinTranslate;
import de.guntram.mcmod.fabrictools.ConfigurationProvider;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;


public class Beenfo implements ClientModInitializer
{
    public static final String MODID = "beenfo";
    public static final String MODNAME = "Beenfo";
    
    public static BlockPos lastHiveResponseBlockPos = null;
    public static int lastHiveResponseHoneyLevel = 0;
    public static int lastHiveResponseBeeCount = 0;
    
    public static Identifier HUD_TEXTURE;
    
    @Environment(EnvType.CLIENT)
    @Override
    public void onInitializeClient() {
        HUD_TEXTURE = new Identifier(Beenfo.MODID, "textures/gui/ingame.png");
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        ConfigurationProvider.register(MODNAME, confHandler);
        confHandler.load(ConfigurationProvider.getSuggestedFile(MODID));
        ClientPlayNetworking.registerGlobalReceiver(BeenfoServer.S2CPacketIdentifierOpen, this::gotHiveInfoOpen);
        ClientPlayNetworking.registerGlobalReceiver(BeenfoServer.S2CPacketIdentifierHud, this::gotHiveInfoHud);
        CrowdinTranslate.downloadTranslations(MODID);
    }

    @Environment(EnvType.CLIENT)
    private void gotHiveInfoOpen(MinecraftClient client, ClientPlayNetworkHandler handler, 
            PacketByteBuf buffer, PacketSender responseSender) {
        int honeyLevel, beeCount;
        ClientPlayerEntity player = client.player;
        List<String> beeNames = new ArrayList<>();
        
        honeyLevel = buffer.readInt();
        beeCount = buffer.readInt();
        for (int i=0; i<beeCount; i++) {
            String beeName=buffer.readString();
            beeNames.add(beeName);
        }
        client.execute(() -> {
            client.setScreen(new BeenfoScreen(null, honeyLevel, beeNames));
        });
    }
    
    @Environment(EnvType.CLIENT)
    private void gotHiveInfoHud(MinecraftClient client, ClientPlayNetworkHandler handler, 
            PacketByteBuf buffer, PacketSender responseSender) {
        int packetVersion = buffer.readInt();
        if (packetVersion == 0) {
            lastHiveResponseHoneyLevel = buffer.readInt();
            lastHiveResponseBeeCount = buffer.readInt();
            lastHiveResponseBlockPos = buffer.readBlockPos();
        }
    }
}
