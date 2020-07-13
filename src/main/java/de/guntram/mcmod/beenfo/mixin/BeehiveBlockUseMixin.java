/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.beenfo.mixin;

import de.guntram.mcmod.beenfo.BeenfoServer;
import net.minecraft.block.BeehiveBlock;
import static net.minecraft.block.BeehiveBlock.HONEY_LEVEL;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 *
 * @author gbl
 */

@Mixin(BeehiveBlock.class)     
public class BeehiveBlockUseMixin {
    
    @Inject(method="onUse", at=@At(value="RETURN", ordinal=1))
    public void onUseStick(BlockState blockState, World world, BlockPos blockPos,
            PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult,
            CallbackInfoReturnable ci) {

        // As we're injecting into the point where super.onUse() gets called,
        // shears and bottles do not reach this point.
        if (!world.isClient()) {
            Item item = playerEntity.getStackInHand(hand).getItem();
            Block block = Block.getBlockFromItem(item);
            // Any item that isn't a block will return Blocks.AIR here
            if (block == Blocks.AIR) {
                int honey = blockState.get(HONEY_LEVEL);
                ListTag tag = null;
                BlockEntity entity = world.getBlockEntity(blockPos);
                if (entity instanceof BeehiveBlockEntity) {
                    BeehiveBlockEntity bbe = (BeehiveBlockEntity) entity;
                    tag = bbe.getBees();
                }
                // System.out.println(honey + " honey, "+bees+" bees"+", item="+playerEntity.getStackInHand(hand).getItem());
                BeenfoServer.sendBeehiveInfo(playerEntity, honey, tag);
            }
        }
    }
}
