package de.guntram.mcmod.beenfo.mixin;

import java.util.List;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemStack.class)
public abstract class TooltipMixin {
    
    @Shadow public abstract boolean isEmpty();
    @Shadow public abstract Item getItem();
    @Shadow public abstract CompoundTag getTag();
    
//    @Inject(method="getTooltip(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List",
    @Inject(method="getTooltip",            
            at=@At("RETURN"), locals=LocalCapture.CAPTURE_FAILHARD)
    private void getTooltipdone(PlayerEntity playerIn, TooltipContext advanced, 
            CallbackInfoReturnable<List> ci,
            List<Text> list) {

        try {
            if (!this.isEmpty() && (this.getItem() == Items.BEEHIVE || this.getItem() == Items.BEE_NEST)) {
                CompoundTag tag = this.getTag();
                if (tag != null) {
                    
                    int honeyLevel = tag.getCompound("BlockStateTag").getInt("honey_level");
                    String honeyLevelStr = tag.getCompound("BlockStateTag").getString("honey_level");  // Some versions of MC (1.15?) seem to use a String ???
                    if (honeyLevelStr != null || !honeyLevelStr.isEmpty()) {
                        try {
                            honeyLevel = Integer.parseInt(honeyLevelStr);
                        } catch (NumberFormatException ex) {
                        }
                    }

                    ListTag bees = tag.getCompound("BlockEntityTag").getList("Bees", 10);
                    int beeCount = bees.size();

                    // Insert our lines in reverse order and always at the beginning of the list,
                    // this way they will appear before the advanced tooltips if enabled.
                    for (int i = 0; i < beeCount; i++)
                    {
                        tag = bees.getCompound(i).getCompound("EntityData");
                        if (tag != null && tag.contains("CustomName", 8))
                        {
                            String beeName = tag.getString("CustomName");
                            list.add(Math.min(1, list.size()), new LiteralText(I18n.translate("tooltip.name", Text.Serializer.fromJson(beeName).getString())));
                        }
                    }

                    // TranslatableText instead of LiteralText(I18n...... has the
                    // problem of not honoring style modifiers.
                    list.add(Math.min(1, list.size()), new LiteralText(I18n.translate("tooltip.bees", beeCount)));
                    list.add(Math.min(1, list.size()), new LiteralText(I18n.translate("tooltip.honey", honeyLevel)));
                }
            }
        } catch (NullPointerException ex) {
            System.out.println("NPE in getTooltipdone");
            try {
                Item item = this.getItem();
                if (item == null) {
                    System.out.println("item is null");
                } else {
                    System.out.println("item is "+this.getItem().getTranslationKey());
                }
            } catch (NullPointerException ex2) {

            }
        }
    }
}