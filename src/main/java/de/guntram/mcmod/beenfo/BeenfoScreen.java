package de.guntram.mcmod.beenfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

public class BeenfoScreen extends Screen {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Beenfo.MODID, "textures/gui/beenfo.png");
    int honeyLevel;
    List<IFormattableTextComponent> beeNames;
    private int x;
    private int y;
    private ItemStack honeyBottle;
    
    BeenfoScreen(Object object, int honeyLevel, List<String> beeNames) {
        super(new TranslationTextComponent("beenfo.screen.title"));
        this.honeyLevel = honeyLevel;
        this.beeNames = new ArrayList<>(beeNames.size());
        for (String beeName: beeNames) {
            if (!(beeName.isEmpty())) {
                this.beeNames.add(ITextComponent.Serializer.func_240643_a_(beeName));
            } else {
                this.beeNames.add(ITextComponent.Serializer.func_240643_a_(beeName));
            }            
        }
        honeyBottle = new ItemStack(Items.HONEY_BOTTLE, 1);
    }

    @Override
    protected void func_231160_c_() {           // init
        super.func_231160_c_();
        int minRows = Math.min(3, beeNames.size());
        int usedHeight = 30 + minRows*30 + 8;
        this.x = (this.field_230708_k_ - 176) / 2;          // width
        this.y = (this.field_230709_l_ - usedHeight) /2;    // height
    }

    @Override
    public void func_230430_a_(MatrixStack stack, int mouseX, int mouseY, float partialTicks) { // render

        if (this.field_230706_i_ == null) {     // client
            // Not sure why this happens, but it does, spuriously, directly after
            // opening the screen
            return;
        }
        func_238651_a_(stack, 0);       // renderBackground
        this.field_230706_i_.getTextureManager().bindTexture(TEXTURE);      // client
        this.func_238474_b_(stack, x, y, 0, 0, 176, 30);                    // blit
        int minRows = Math.max(3, beeNames.size());
        for (int i=0; i<minRows; i++) {
            func_238474_b_(stack, x, y+30+i*30, 0, 30, 176, 30);
            if (i < beeNames.size()) {
                func_238474_b_(stack, x+9, y+33+(i)*(30), 0, 166, 22, 22);
            }
        }
        this.func_238474_b_(stack, x, y+30+minRows*30, 0, 157, 176, 8);
        
        for (int i=Math.max(5, honeyLevel); i<9; i++) {
            this.func_238474_b_(stack, x+7+i*18, y+7, 8, 64, 18, 18);
        }

        // Do everything that needs our texture above this because drawing a text will bind a different one

        for (int i=0; i<beeNames.size(); i++) {
            if (beeNames.get(i) != null) {
                field_230712_o_.func_238422_b_(stack, beeNames.get(i).func_241878_f(), x+48, y+32+(i)*(30)+8, 0x000000);      //fontRenderer.draw
            }
        }
        func_230926_e_(200);                                                // setZLevel?
        field_230707_j_.zLevel = 200.0F;                                    // itemRenderer
        for (int i=0; i<honeyLevel; i++) {
            field_230707_j_.renderItemIntoGUI(honeyBottle, x+8+(i*18), y+8);
        }
        func_230926_e_(0);
        field_230707_j_.zLevel = 0.0F;
    }
    
    @Override
    public boolean func_231046_a_(int keyCode, int scanCode, int modifiers) { // keyPressed
        if (super.func_231046_a_(keyCode, scanCode, modifiers)) {
           return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE || this.field_230706_i_.gameSettings.keyBindInventory.matchesKey(keyCode, scanCode)) {
              this.field_230706_i_.player.closeScreen();                    // client
              return true;
        }
        return false;
    }
    
    @Override
    public boolean func_231177_au__() {                                     // isPauseScreen
        return false;
    }
}
