package de.guntram.mcmod.beenfo;

import de.guntram.mcmod.beenfo.mixin.TooltipMixin;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;

@Mod("beenfo")
public class Beenfo implements Consumer<NetworkEvent>
{
    public static final String MODID = "beenfo";
    public static final String MODNAME = "Beenfo";
    
    public static final ResourceLocation C2SPacketIdentifier = new ResourceLocation(MODID, "c2s");    
    public static final ResourceLocation S2CPacketIdentifier = new ResourceLocation(MODID, "s2c");
    
    EventNetworkChannel channel;

    public Beenfo() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);        
    }
    
    public void init(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new TooltipMixin());
        channel = NetworkRegistry.newEventChannel(
            S2CPacketIdentifier,
            () -> "",
            NetworkRegistry.ACCEPTVANILLA::equals,
            NetworkRegistry.ACCEPTVANILLA::equals
        );        
        channel.addListener(this);
    }

    @Override
    public void accept(NetworkEvent event) {
        NetworkEvent.Context context = event.getSource().get();
        context.enqueueWork(() -> {gotHiveInfo(null, event.getPayload());});
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
