package de.guntram.mcmod.beenfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class BeenfoClient implements Consumer<NetworkEvent>
{
    @Override
    public void accept(NetworkEvent event) {
        NetworkEvent.Context context = event.getSource().get();
        if (event.getPayload() != null) {
            context.enqueueWork(() -> {
                gotHiveInfo(null, event.getPayload());
            });
        }
        context.setPacketHandled(true);
    }
    
    private void gotHiveInfo(Object context, PacketBuffer buffer) {
        int honeyLevel, beeCount;
        ClientPlayerEntity player = Minecraft.getInstance().player;
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
        Minecraft.getInstance().displayGuiScreen(new BeenfoScreen(null, honeyLevel, beeNames));
    }
}
