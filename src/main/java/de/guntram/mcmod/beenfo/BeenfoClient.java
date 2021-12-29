package de.guntram.mcmod.beenfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;


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
    
    private void gotHiveInfo(Object context, FriendlyByteBuf buffer) {
        int honeyLevel, beeCount;
        LocalPlayer player = Minecraft.getInstance().player;
        List<String> beeNames = new ArrayList<>();
        
        honeyLevel = buffer.readInt();
        beeCount = buffer.readInt();

        for (int i=0; i<beeCount; i++) {
            String beeName=buffer.readUtf();
            beeNames.add(beeName);
        }
        Minecraft.getInstance().setScreen(new BeenfoScreen(null, honeyLevel, beeNames));
    }
}
