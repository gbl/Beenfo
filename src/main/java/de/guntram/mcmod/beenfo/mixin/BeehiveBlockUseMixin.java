/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.beenfo.mixin;

import de.guntram.mcmod.beenfo.BeenfoServer;
import static net.minecraft.block.BeehiveBlock.HONEY_LEVEL;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

        World world = event.getWorld();
        BlockPos blockPos = event.getPos();
        PlayerEntity playerEntity = event.getPlayer();
        Hand hand = event.getHand();
        BlockState blockState = world.getBlockState(blockPos);
        
        if (!world.isRemote()) {
            
            Item item = event.getItemStack().getItem();
            Block block = Block.getBlockFromItem(item);
            // Any item that isn't a block will return Blocks.AIR here
            if (block == Blocks.AIR && item != Items.SHEARS && item != Items.GLASS_BOTTLE) {
                ListNBT tag = null;
                TileEntity entity = world.getTileEntity(blockPos);
                if (entity instanceof BeehiveTileEntity) {
                    BeehiveTileEntity bbe = (BeehiveTileEntity) entity;
                    tag = bbe.getBees();
                } else {
                    return;
                }
                int honey = 0;
                if (blockState.hasProperty(HONEY_LEVEL)) {       // hasProperty
                    honey = blockState.get(HONEY_LEVEL);
                }
                // System.out.println(honey + " honey, "+tag.size()+" bees"+", item="+playerEntity.getHeldItem(hand).getItem());
                BeenfoServer.sendBeehiveInfo(playerEntity, honey, tag);
                event.setResult(Event.Result.ALLOW);
            }
        }
    }
}
