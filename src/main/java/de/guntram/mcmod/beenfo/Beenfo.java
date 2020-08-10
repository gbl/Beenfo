package de.guntram.mcmod.beenfo;

import de.guntram.mcmod.beenfo.mixin.BeehiveBlockUseMixin;
import de.guntram.mcmod.beenfo.mixin.TooltipMixin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import org.apache.commons.lang3.tuple.Pair;

@Mod("beenfo")
public class Beenfo
{
    public static final String MODID = "beenfo";
    public static final String MODNAME = "Beenfo";
    
    public static final ResourceLocation C2SPacketIdentifier = new ResourceLocation(MODID, "c2s");    
    public static final ResourceLocation S2CPacketIdentifier = new ResourceLocation(MODID, "s2c");
    
    EventNetworkChannel channel;

    public Beenfo() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);        
    }
    
    public void init(final FMLCommonSetupEvent event) {
        channel = NetworkRegistry.newEventChannel(
            S2CPacketIdentifier,
            () -> "",
            (a) -> true,
            (a) -> true
        );
        if (FMLEnvironment.dist.isClient()) {
            MinecraftForge.EVENT_BUS.register(new TooltipMixin());
            channel.addListener(new BeenfoClient());
        }
        MinecraftForge.EVENT_BUS.register(new BeehiveBlockUseMixin());
    }
}
