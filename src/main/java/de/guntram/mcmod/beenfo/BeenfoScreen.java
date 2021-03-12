package de.guntram.mcmod.beenfo;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class BeenfoScreen extends Screen {

    private static final Identifier TEXTURE = new Identifier(Beenfo.MODID, "textures/gui/beenfo.png");
    int honeyLevel;
    List<Text> beeNames;
    private int x;
    private int y;
    private ItemStack honeyBottle;
    
    BeenfoScreen(Object object, int honeyLevel, List<String> beeNames) {
        super(new TranslatableText("beenfo.screen.title"));
        this.honeyLevel = honeyLevel;
        this.beeNames = new ArrayList<>(beeNames.size());
        for (String beeName: beeNames) {
            if (!(beeName.isEmpty())) {
                this.beeNames.add(Text.Serializer.fromJson(beeName));
            } else {
                this.beeNames.add(Text.Serializer.fromJson(beeName));
            }            
        }
        honeyBottle = new ItemStack(Items.HONEY_BOTTLE, 1);
    }

    @Override
    protected void init() {
        super.init();
        int minRows = Math.min(3, beeNames.size());
        int usedHeight = 30 + minRows*30 + 8;
        this.x = (this.width - 176) / 2;
        this.y = (this.height - usedHeight) /2;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {

        if (this.client == null) {
            // Not sure why this happens, but it does, spuriously, directly after
            // opening the screen
            return;
        }
        renderBackground(stack, 0);
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.drawTexture(stack, x, y, 0, 0, 176, 30);
        int minRows = Math.max(3, beeNames.size());
        for (int i=0; i<minRows; i++) {
            drawTexture(stack, x, y+30+i*30, 0, 30, 176, 30);
            if (i < beeNames.size()) {
                drawTexture(stack, x+9, y+33+(i)*(30), 0, 166, 22, 22);
            }
        }
        this.drawTexture(stack, x, y+30+minRows*30, 0, 157, 176, 8);
        
        for (int i=Math.max(5, honeyLevel); i<9; i++) {
            this.drawTexture(stack, x+7+i*18, y+7, 8, 64, 18, 18);
        }

        // Do everything that needs our texture above this because drawing a text will bind a different one

        for (int i=0; i<beeNames.size(); i++) {
            if (beeNames.get(i) != null) {
                textRenderer.draw(stack, beeNames.get(i).asOrderedText(), x+48, y+32+(i)*(30)+8, 0x000000);
            }
        }
        setZOffset(200);
        itemRenderer.zOffset = 200.0F;        
        for (int i=0; i<honeyLevel; i++) {
            itemRenderer.renderInGuiWithOverrides(honeyBottle, x+8+(i*18), y+8);
        }
        setZOffset(0);
        itemRenderer.zOffset = 0.0F;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
           return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE || this.client.options.keyInventory.matchesKey(keyCode, scanCode)) {
              this.client.player.closeScreen();
              return true;
        }
        return false;
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
