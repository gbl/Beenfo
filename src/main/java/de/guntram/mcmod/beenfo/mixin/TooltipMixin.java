package de.guntram.mcmod.beenfo.mixin;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class TooltipMixin {
    
   @SubscribeEvent
    public void getTooltipdone(final ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<ITextComponent> list = event.getToolTip();
        try {
            if (!stack.isEmpty()) {
                CompoundNBT tag = stack.getTag();
                if (tag == null)
                    return;
                CompoundNBT bsTag = tag.getCompound("BlockStateTag");
                if (bsTag == null || !bsTag.contains("honey_level"))
                    return;
                CompoundNBT beTag = tag.getCompound("BlockEntityTag");
                if (beTag == null || !beTag.contains("Bees"))
                    return;
                
                String honeyLevel = bsTag.getString("honey_level");  // wtf this is a string ???
                if (honeyLevel == null || honeyLevel.isEmpty()) {
                    honeyLevel = "0";
                }

                ListNBT bees = beTag.getList("Bees", 10);
                int beeCount = bees.size();

                // Insert our lines in reverse order and always at the beginning of the list,
                // this way they will appear before the advanced tooltips if enabled.
                for (int i = 0; i < beeCount; i++)
                {
                    tag = bees.getCompound(i).getCompound("EntityData");
                    if (tag != null && tag.contains("CustomName", 8))
                    {
                        String beeName = tag.getString("CustomName");
                        list.add(Math.min(1, list.size()), new TranslationTextComponent("tooltip.name", ITextComponent.Serializer.fromJson(beeName).getString()));
                    }
                }

                list.add(Math.min(1, list.size()), new TranslationTextComponent("tooltip.bees", beeCount));
                list.add(Math.min(1, list.size()), new TranslationTextComponent("tooltip.honey", honeyLevel));
            }
        } catch (NullPointerException ex) {
            System.out.println("NPE in getTooltipdone");
            try {
                Item item = stack.getItem();
                if (item == null) {
                    System.out.println("item is null");
                } else {
                    System.out.println("item is "+stack.getItem().getTranslationKey());
                }
            } catch (NullPointerException ex2) {

            }
        }
    }
}