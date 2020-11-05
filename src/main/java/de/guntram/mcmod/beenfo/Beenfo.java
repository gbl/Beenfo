package de.guntram.mcmod.beenfo;

import de.guntram.mcmod.crowdintranslate.CrowdinTranslate;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;


public class Beenfo implements ClientModInitializer
{
    public static final String MODID = "beenfo";
    public static final String MODNAME = "Beenfo";
    
    public static final Identifier C2SPacketIdentifier = new Identifier(MODID, "c2s");    
    public static final Identifier S2CPacketIdentifier = new Identifier(MODID, "s2c");
    
    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(Beenfo.S2CPacketIdentifier, this::gotHiveInfo);
        CrowdinTranslate.downloadTranslations(MODID);
    }

    private void gotHiveInfo(PacketContext context, PacketByteBuf buffer) {
        int honeyLevel, beeCount;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        List<String> beeNames = new ArrayList<>();
        
        honeyLevel = buffer.readInt();
        beeCount = buffer.readInt();
//        String playerMessage = I18n.translate("tooltip.bees", new Object[] { beeCount})
//                +", "+I18n.translate("tooltip.honey", new Object[] { honeyLevel});
//        player.sendMessage(new LiteralText(playerMessage), false);
        for (int i=0; i<beeCount; i++) {
            String beeName=buffer.readString();
            beeNames.add(beeName);
/*
            if (!(beeName.isEmpty())) {
                player.sendMessage(new LiteralText(I18n.translate("tooltip.name",
                        Text.Serializer.fromJson(beeName).getString())), false);
            } else {
                player.sendMessage(new LiteralText(I18n.translate("tooltip.unnamed")), false);
            }
*/
        }
        MinecraftClient.getInstance().openScreen(new BeenfoScreen(null, honeyLevel, beeNames));
    }
}
