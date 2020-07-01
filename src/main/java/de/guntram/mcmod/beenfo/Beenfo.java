package de.guntram.mcmod.beenfo;

import de.guntram.mcmod.beenfo.mixin.TooltipMixin;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("beenfo")
public class Beenfo
{
    public static final String MODID = "beenfo";
    public static final String MODNAME = "Beenfo";
    public static final String VERSION = "1.15-fabric0.4.23-1.0.3";

    public Beenfo() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);        
    }
    
    public void init(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new TooltipMixin());
    }
}
