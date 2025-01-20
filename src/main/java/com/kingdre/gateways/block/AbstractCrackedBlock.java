package com.kingdre.gateways.block;

import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
import com.kingdre.gateways.block.entity.ResonanceConduitBlockEntity;
import com.kingdre.gateways.item.GatewaysItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SnowballItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractCrackedBlock extends Block {
    public AbstractCrackedBlock(Settings settings) {
        super(settings);
    }

    public abstract BlockState getFixedBlock();

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            if (!player.getActiveHand().equals(hand))
                return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

            if(stack.getItem().equals(Items.AMETHYST_SHARD)) {
                world.setBlockState(pos, this.getFixedBlock());
                stack.decrement(1);
                world.playSound(
                        null,
                        pos,
                        SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                        SoundCategory.BLOCKS,
                        50f,
                        1f
                );
                return ItemActionResult.SUCCESS;
            }
        }


        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {

        if(!world.isClient()) {
            if(entity.getType().equals(EntityType.ITEM)) {
                ItemEntity item = (ItemEntity) entity;
                ItemStack stack = item.getStack();

                if(stack.getItem().equals(Items.AMETHYST_SHARD)) {
                    stack.decrement(1);
                    item.setStack(stack);

                    world.playSound(
                            null,
                            pos,
                            SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                            SoundCategory.BLOCKS,
                            50f,
                            1f
                    );

                    world.setBlockState(pos, this.getFixedBlock());
                }
            }
        }

        super.onLandedUpon(world, state, pos, entity, fallDistance);
    }
}
