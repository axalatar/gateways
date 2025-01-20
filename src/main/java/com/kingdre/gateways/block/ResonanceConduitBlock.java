package com.kingdre.gateways.block;

import com.kingdre.gateways.GatewaysComponents;
import com.kingdre.gateways.block.entity.GatewayHubBlockEntity;
import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
import com.kingdre.gateways.block.entity.ResonanceConduitBlockEntity;
import com.kingdre.gateways.item.GatewaysItems;
import com.kingdre.gateways.item.TuningForkItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ResonanceConduitBlock extends BlockWithEntity {
    private static final BooleanProperty POWERED = Properties.POWERED;
    private static final VoxelShape SHAPE = Block.createCuboidShape(4, 0, 4, 12, 16, 12);


    protected ResonanceConduitBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager.getDefaultState().with(POWERED, false)
        );
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        BlockEntity entity = world.getBlockEntity(pos);

        if(!state.isOf(newState.getBlock()) && entity instanceof ResonanceConduitBlockEntity conduit && conduit.heldItem != null) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), conduit.heldItem);
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ResonanceConduitBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            Hand hand = player.getActiveHand();
            if (!hand.equals(player.preferredHand))
                return ActionResult.PASS;

            BlockEntity entity = world.getBlockEntity(pos);
            if(entity == null || entity.getType() != GatewaysBlockEntities.RESONANCE_CONDUIT_BLOCK_ENTITY) return ActionResult.PASS;

            ResonanceConduitBlockEntity conduitEntity = (ResonanceConduitBlockEntity) entity;

            ItemStack item = player.getStackInHand(hand);
            Item itemType = item.getItem();
            if(itemType.equals(GatewaysItems.TUNING_FORK) && conduitEntity.heldItem == ItemStack.EMPTY) {

                conduitEntity.heldItem = item;
                conduitEntity.markDirty();
                world.updateListeners(pos, state, state, 0);

                player.setStackInHand(player.getActiveHand(), Items.AIR.getDefaultStack());
                return ActionResult.SUCCESS;
            }
            else if(itemType.equals(Items.AIR) && conduitEntity.heldItem != ItemStack.EMPTY) {
                player.setStackInHand(player.getActiveHand(), conduitEntity.heldItem);

                conduitEntity.heldItem = ItemStack.EMPTY;
                conduitEntity.markDirty();
                world.updateListeners(pos, state, state, 0);

                return ActionResult.SUCCESS;
            }
        }


        return ActionResult.PASS;
    }



    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if(world.isClient()) return;

        boolean powered = world.getEmittedRedstonePower(pos.down(), Direction.DOWN) > 0;
        boolean alreadyPowered = state.get(POWERED);
        if (!alreadyPowered) {
            if (powered) {
                BlockEntity entity = world.getBlockEntity(pos);
                if(entity == null || entity.getType() != GatewaysBlockEntities.RESONANCE_CONDUIT_BLOCK_ENTITY) return;

                ResonanceConduitBlockEntity conduitEntity = (ResonanceConduitBlockEntity) entity;
                ItemStack item = conduitEntity.heldItem;

                if(item != ItemStack.EMPTY) {
                    TuningForkItem.giveFrequency(pos.down(), world, item.get(GatewaysComponents.FREQUENCY));
                    world.setBlockState(pos, state.with(POWERED, true), Block.NO_REDRAW);
                    world.playSound(
                            null,
                            pos,
                            SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                            SoundCategory.BLOCKS,
                            50f,
                            1f
                    );
                }
            }
        }
        else if (!powered) world.setBlockState(pos, state.with(POWERED, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(ResonanceConduitBlock::new);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
