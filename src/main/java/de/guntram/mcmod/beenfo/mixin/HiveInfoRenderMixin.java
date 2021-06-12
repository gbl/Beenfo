/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.beenfo.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import de.guntram.mcmod.beenfo.Beenfo;
import de.guntram.mcmod.beenfo.BeenfoServer;
import de.guntram.mcmod.beenfo.config.ConfigurationHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import static net.minecraft.state.property.Properties.HONEY_LEVEL;
import net.minecraft.text.OrderedText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 *
 * @author gbl
 */
@Mixin(InGameHud.class)
public class HiveInfoRenderMixin extends DrawableHelper {

    @Shadow @Final private MinecraftClient client;
    private long lastHiveRequestTime = 0;
    private BlockPos lastHiveRequestBlockPos = null;
    
    @Inject(method="renderStatusEffectOverlay", at=@At("RETURN"))
    
    private void afterRenderStatusEffects(MatrixStack stack, CallbackInfo ci) {
        
        if (!ConfigurationHandler.getShowPopup()) {
            return;
        }
        
        Entity entity = this.client.getCameraEntity();
        HitResult blockHit = entity.raycast(20, 0, false);
        if (blockHit != null && blockHit.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult)blockHit).getBlockPos();
            
            BlockState state = this.client.world.getBlockState(blockPos);
            if (!state.contains(HONEY_LEVEL)) {
                return;
            }
            
            long now = System.currentTimeMillis();
            boolean sameblock = blockPos.equals(lastHiveRequestBlockPos);
            if (now > lastHiveRequestTime + 100 || !sameblock) {
                if (!sameblock) {
                    Beenfo.lastHiveResponseBeeCount = 0;
                }
                lastHiveRequestBlockPos = blockPos;
                lastHiveRequestTime = now;

                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeInt(0);        // packet version
                buf.writeBlockPos(blockPos);
                ClientSidePacketRegistry.INSTANCE.sendToServer(BeenfoServer.C2SPacketIdentifier, buf);
            }
            
            int honey = state.get(HONEY_LEVEL);
            RenderSystem.setShaderTexture(0, Beenfo.HUD_TEXTURE);
            int x=(client.getWindow().getScaledWidth()-82)*ConfigurationHandler.getXPercent()/100;
            int y=(client.getWindow().getScaledHeight()-59)*ConfigurationHandler.getYPercent()/100;
            this.drawTexture(stack, x, y, 0, 0, 82, 59);
            
            if (honey >= 1) { this.drawTexture(stack, x+17, y+16, 84, 17, 6, 7); }
            if (honey >= 2) { this.drawTexture(stack, x+24, y+22, 84, 17, 6, 7); }
            if (honey >= 3) { this.drawTexture(stack, x+31, y+16, 84, 17, 6, 7); }
            if (honey >= 4) { this.drawTexture(stack, x+38, y+22, 84, 17, 6, 7); }
            if (honey >= 5) { this.drawTexture(stack, x+51, y+16, 83, 34, 14, 13); }
            
            if (Beenfo.lastHiveResponseBeeCount >= 1) { this.drawTexture(stack, x+14, y+37, 83, 2, 13, 12); }
            if (Beenfo.lastHiveResponseBeeCount >= 2) { this.drawTexture(stack, x+34, y+37, 83, 2, 13, 12); }
            if (Beenfo.lastHiveResponseBeeCount >= 3) { this.drawTexture(stack, x+54, y+37, 83, 2, 13, 12); }
            
            OrderedText orderedText = state.getBlock().getName().asOrderedText();
            client.textRenderer.draw(stack, orderedText, (float)(x + 41 - client.textRenderer.getWidth(orderedText) / 2), y+5, 0x404040);
        }
    }
}
    