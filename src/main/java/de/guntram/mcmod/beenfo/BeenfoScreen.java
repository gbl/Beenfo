package de.guntram.mcmod.beenfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

public class BeenfoScreen extends Screen {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Beenfo.MODID, "textures/gui/beenfo.png");
    int honeyLevel;
    List<MutableComponent> beeNames;
    private int x;
    private int y;
    private ItemStack honeyBottle;
    
    BeenfoScreen(Object object, int honeyLevel, List<String> beeNames) {
        super(new TranslatableComponent("beenfo.screen.title"));
        this.honeyLevel = honeyLevel;
        this.beeNames = new ArrayList<>(beeNames.size());
        for (String beeName: beeNames) {
            if (!(beeName.isEmpty())) {
                this.beeNames.add(Component.Serializer.fromJson(beeName));
            } else {
                this.beeNames.add(Component.Serializer.fromJson(beeName));
            }            
        }
        honeyBottle = new ItemStack(Items.HONEY_BOTTLE, 1);
    }

    @Override
    protected void init() {           // init
        super.init();
        int minRows = Math.min(3, beeNames.size());
        int usedHeight = 30 + minRows*30 + 8;
        this.x = (this.width - 176) / 2;          // width
        this.y = (this.height - usedHeight) /2;    // height
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) { // render

        if (this.minecraft == null) {     // client
            // Not sure why this happens, but it does, spuriously, directly after
            // opening the screen
            return;
        }
        renderBackground(stack, 0);       // renderBackground
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.blit(stack, x, y, 0, 0, 176, 30);
        int minRows = Math.max(3, beeNames.size());
        for (int i=0; i<minRows; i++) {
            blit(stack, x, y+30+i*30, 0, 30, 176, 30);
            if (i < beeNames.size()) {
                blit(stack, x+9, y+33+(i)*(30), 0, 166, 22, 22);
            }
        }
        this.blit(stack, x, y+30+minRows*30, 0, 157, 176, 8);
        
        for (int i=Math.max(5, honeyLevel); i<9; i++) {
            this.blit(stack, x+7+i*18, y+7, 8, 64, 18, 18);
        }

        // Do everything that needs our texture above this because drawing a text will bind a different one

        for (int i=0; i<beeNames.size(); i++) {
            if (beeNames.get(i) != null) {
                font.draw(stack, beeNames.get(i).getString(), x+48, y+32+(i)*(30)+8, 0x000000);      //fontRenderer.draw
            }
        }
        setBlitOffset(200);                                                // setZLevel?
        itemRenderer.blitOffset = 200.0F;                                    // itemRenderer
        for (int i=0; i<honeyLevel; i++) {
            itemRenderer.renderGuiItem(honeyBottle, x+8+(i*18), y+8);
        }
        setBlitOffset(0);
        itemRenderer.blitOffset = 0.0F;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) { // keyPressed
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
           return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE || this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
              this.minecraft.player.closeContainer();
              return true;
        }
        return false;
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
