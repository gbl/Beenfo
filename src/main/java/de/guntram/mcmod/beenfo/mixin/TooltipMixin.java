package de.guntram.mcmod.beenfo.mixin;

import java.util.List;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
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
            at=@At("RETURN"), locals=LocalCapture.CAPTURE_FAILHARD, cancellable=true)
    private void getTooltipdone(PlayerEntity playerIn, TooltipContext advanced, 
            CallbackInfoReturnable<List> ci,
            List<Text> list) {

        try {
            if (!this.isEmpty() && (this.getItem() == Items.BEE_HIVE || this.getItem() == Items.BEE_NEST)) {
                CompoundTag tag = this.getTag();
                if (tag != null) {
                    String honeyLevel = tag.getCompound("BlockStateTag").getString("honey_level");  // wtf this is a string ???
                    int beeCount   = tag.getCompound("BlockEntityTag").getList("Bees", 10).size();
                    list.add(new LiteralText(honeyLevel+" honey"));
                    list.add(new LiteralText(beeCount+   " bees"));
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
        ci.setReturnValue(list);
    }
}