package de.guntram.mcmod.beenfo.mixin;

import java.util.List;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class TooltipMixin {
    
   @SubscribeEvent
    public void getTooltipdone(final ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> list = event.getToolTip();
        try {
            if (!stack.isEmpty()) {
                CompoundTag tag = stack.getTag();
                if (tag == null)
                    return;
                CompoundTag bsTag = tag.getCompound("BlockStateTag");
                if (bsTag == null || !bsTag.contains("honey_level"))
                    return;
                CompoundTag beTag = tag.getCompound("BlockEntityTag");
                if (beTag == null || !beTag.contains("Bees"))
                    return;
                
                int honeyLevel = bsTag.getInt("honey_level");
                String honeyLevelStr = bsTag.getString("honey_level");  // wtf this is a string ???
                if (honeyLevelStr != null && !honeyLevelStr.isEmpty()) {
                    try {
                        honeyLevel = Integer.parseInt(honeyLevelStr);
                    } catch (NumberFormatException ex) {
                    }
                }

                ListTag bees = beTag.getList("Bees", 10);
                int beeCount = bees.size();

                // Insert our lines in reverse order and always at the beginning of the list,
                // this way they will appear before the advanced tooltips if enabled.
                for (int i = 0; i < beeCount; i++)
                {
                    tag = bees.getCompound(i).getCompound("EntityData");
                    if (tag != null && tag.contains("CustomName", 8))
                    {
                        String beeName = tag.getString("CustomName");
                        list.add(Math.min(1, list.size()), Component.literal(I18n.get("tooltip.name", Component.Serializer.fromJson(beeName).getString())));
                    }
                }

                list.add(Math.min(1, list.size()), Component.literal(I18n.get("tooltip.bees", beeCount)));
                list.add(Math.min(1, list.size()), Component.literal(I18n.get("tooltip.honey", honeyLevel)));
            }
        } catch (NullPointerException ex) {
            System.out.println("NPE in getTooltipdone");
            try {
                Item item = stack.getItem();
                if (item == null) {
                    System.out.println("item is null");
                } else {
                    System.out.println("item is "+stack.getItem().getDescriptionId());
                }
            } catch (NullPointerException ex2) {

            }
        }
    }
}