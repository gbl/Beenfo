/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.beenfo.mixin;

import de.guntram.mcmod.beenfo.BeenfoServer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import static net.minecraft.world.level.block.BeehiveBlock.HONEY_LEVEL;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 *
 * @author gbl
 */

public class BeehiveBlockUseMixin {
    
    @SubscribeEvent
    public void onUseStick(PlayerInteractEvent.RightClickBlock event) {

        Level world = event.getWorld();
        BlockPos blockPos = event.getPos();
        Player playerEntity = event.getPlayer();
        InteractionHand hand = event.getHand();
        BlockState blockState = world.getBlockState(blockPos);
        
        if (!world.isClientSide()) {
            
            Item item = event.getItemStack().getItem();
            Block block = Block.byItem(item);
            // Any item that isn't a block will return Blocks.AIR here
            if (block == Blocks.AIR && item != Items.SHEARS && item != Items.GLASS_BOTTLE) {
                ListTag tag = null;
                BlockEntity entity = world.getBlockEntity(blockPos);
                if (entity instanceof BeehiveBlockEntity) {
                    BeehiveBlockEntity bbe = (BeehiveBlockEntity) entity;
                    tag = bbe.writeBees();
                } else {
                    return;
                }
                int honey = 0;
                if (blockState.hasProperty(HONEY_LEVEL)) {       // hasProperty
                    honey = blockState.getValue(HONEY_LEVEL);
                }
                // System.out.println(honey + " honey, "+tag.size()+" bees"+", item="+playerEntity.getHeldItem(hand).getItem());
                BeenfoServer.sendBeehiveInfo(playerEntity, honey, tag);
                event.setResult(Event.Result.ALLOW);
            }
        }
    }
}
