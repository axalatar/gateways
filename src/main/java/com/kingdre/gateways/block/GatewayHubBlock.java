package com.kingdre.gateways.block;

import com.kingdre.gateways.Gateways;
import com.kingdre.gateways.GatewaysNetworking;
import com.kingdre.gateways.block.entity.GatewayHubBlockEntity;
import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
import com.mojang.serialization.MapCodec;
import foundry.veil.api.client.registry.PostPipelineStageRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GatewayHubBlock extends BlockWithEntity {
    private static final BooleanProperty POWERED = Properties.POWERED;
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty OPEN = BooleanProperty.of("open");


    private final static int MAX_PAD_SIDE_LENGTH = 7;
    // must be an odd number
    // probably a good idea to have this
    // is there a better way to do a const in java?

    protected GatewayHubBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager.getDefaultState().with(POWERED, false).with(FACING, Direction.NORTH).with(OPEN, false)
        );

    }
    /*
    GRAPHICS CODE:
    TODO particles on teleport
    TODO screenshake and flash

    NON-GRAPHICS CODE:
    actually done this time :D

    OTHER:
    done :D

    PRESENTATION:
    TODO video
     */

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED).add(FACING).add(OPEN);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GatewayHubBlockEntity(pos, state);
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            Hand hand = player.getActiveHand();
            if (!hand.equals(player.preferredHand))
                return ActionResult.PASS;

            ItemStack handStack = player.getStackInHand(hand);

            if(handStack.isEmpty()) {
                ActionResult result = attemptTeleport(state, world, pos);

                return result;
            }
        }
        return ActionResult.PASS;
    }


    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient()) return;


        boolean powered = world.isReceivingRedstonePower(pos);
        boolean alreadyPowered = state.get(POWERED);
        if (!alreadyPowered) {
            if (powered) {
                this.attemptTeleport(state, world, pos);
                world.setBlockState(pos, state.with(POWERED, true), Block.NO_REDRAW);
            }
        } else if (!powered) world.setBlockState(pos, state.with(POWERED, false));
    }

    private void debug(World world, String message) {
        world.getPlayers().forEach((playerEntity -> {
            playerEntity.sendMessage(Text.of(message));
        }));
    }


    /**
     * Attempt to teleport blocks and entities from the gateway, validates the gateway before teleportation.
     * Also updates the state of the hub. Returns whether the click was successful
     */
    public ActionResult attemptTeleport(BlockState state, World world, BlockPos pos) {
        if (!world.isClient()) {

            BlockEntity entity = world.getBlockEntity(pos);
            if (entity == null || !entity.getType().equals(GatewaysBlockEntities.GATEWAY_HUB_BLOCK_ENTITY)) {
                world.setBlockState(pos, state.with(OPEN, false));
                return ActionResult.PASS;
            }

            GatewayHubBlockEntity hubEntity = (GatewayHubBlockEntity) entity;

            List<Integer> frequency = hubEntity.heldFrequency;
            if (frequency.isEmpty()) {
                world.setBlockState(pos, state.with(OPEN, false));
                return ActionResult.PASS;
            }

            MinecraftServer server = ((ServerWorld) world).getServer();

            boolean open = state.get(OPEN);
            BlockBox fromBlockBox = validateGatewayPad(world, pos, true);
            if (fromBlockBox == null) {
                world.setBlockState(pos, state.with(OPEN, false));
                return ActionResult.PASS;
            }

            Box fromBox = Box.from(fromBlockBox);
          //  debug(world, String.valueOf(fromBlockBox));

            BlockPos tunedTo = BlockPos.ofFloored(frequency.get(0), frequency.get(1), frequency.get(2));
            BlockEntity destinationEntity = world.getBlockEntity(tunedTo);

            if (destinationEntity == null || !destinationEntity.getType().equals(GatewaysBlockEntities.GATEWAY_HUB_BLOCK_ENTITY)) {
                world.setBlockState(pos, state.with(OPEN, false));
                return ActionResult.PASS;
            }

//            ((ServerWorld) world).getChunkManager().getChunk(0, 0)

            BlockBox toBox = validateGatewayPad(world, tunedTo, false);

            if (toBox == null) {
                world.setBlockState(pos, state.with(OPEN, false));
                return ActionResult.PASS;
            }


            if (fromBlockBox.intersects(toBox)) {
                world.setBlockState(pos, state.with(OPEN, false));
                return ActionResult.PASS;
            }

            if (fromBlockBox.getBlockCountX() > toBox.getBlockCountX()) {
                world.setBlockState(pos, state.with(OPEN, false));
                return ActionResult.PASS;
            }

            if (!open) {
                world.setBlockState(pos, state.with(OPEN, true));
                return ActionResult.SUCCESS;
            }


            StructureTemplate template = new StructureTemplate();

            Vec3d difference = new Vec3d(toBox.getMinX(), toBox.getMinY(), toBox.getMinZ())
                    .subtract(fromBlockBox.getMinX(), fromBlockBox.getMinY(), fromBlockBox.getMinZ());

            BlockBox centeredToBox = fromBlockBox.offset((int) difference.x, (int) difference.y, (int) difference.z);

            template.saveFromWorld(
                    world,
                    BlockPos.ofFloored(fromBlockBox.getMinX(), fromBlockBox.getMinY(), fromBlockBox.getMinZ()),
                    fromBlockBox.getDimensions(),
                    true,
                    null
            );

            template.place(
                    (ServerWorld) world,
                    BlockPos.ofFloored(centeredToBox.getMinX(), centeredToBox.getMinY(), centeredToBox.getMinZ()),
                    BlockPos.ofFloored(toBox.getMinX(), toBox.getMinY(), toBox.getMinZ()),
                    new StructurePlacementData(),
                    world.getRandom(),
                    Block.NOTIFY_ALL
            );

//                debug(world, fromBlockBox.getMinX() + " " + fromBlockBox.getMinY() + " " + fromBlockBox.getMinZ());
//                debug(world, fromBlockBox.getMaxX() + " " + fromBlockBox.getMaxY() + " " + fromBlockBox.getMaxZ());

            CuboidBlockIterator iterator = new CuboidBlockIterator(
                    fromBlockBox.getMinX(),
                    fromBlockBox.getMinY(),
                    fromBlockBox.getMinZ(),
                    fromBlockBox.getMaxX() - 1,
                    fromBlockBox.getMaxY() - 1,
                    fromBlockBox.getMaxZ() - 1
            );

//                debug(world, fromBox.minX + " " + fromBox.minY + " " + fromBox.minZ);
//                debug(world, fromBox.maxX + " " + fromBox.maxY + " " + fromBox.maxZ);

            int i = 0;
            // putting the extra i here because i don't trust the block iterator enough

            while (iterator.step()) {
                if (i > Math.pow(MAX_PAD_SIDE_LENGTH, 3)) break; // just to be safe
                BlockPos fromPos = BlockPos.ofFloored(iterator.getX(), iterator.getY(), iterator.getZ());

                BlockEntity fromEntity = world.getBlockEntity(fromPos);

                if (fromEntity != null)
                    fromEntity.markRemoved();

                world.setBlockState(
                        fromPos,
                        Blocks.AIR.getDefaultState()
                );


                i++;
            }

            List<ServerPlayerEntity> checked = new ArrayList<>();

            world.getEntitiesByType(TypeFilter.instanceOf(Entity.class), fromBox
                            .withMaxX(fromBox.maxX - 1)
                            .withMaxY(fromBox.maxY - 1)
                            .withMaxZ(fromBox.maxZ - 1),
                    o -> true).forEach((fromEntity -> {
                if (fromEntity.getType() == EntityType.PLAYER) {
                    ServerPlayerEntity player = (ServerPlayerEntity) fromEntity;

                    player.requestTeleportOffset(difference.getX(), difference.getY(), difference.getZ());
                    BlockPos fromUp = pos.up();
                    BlockPos toUp = tunedTo.up();

                    ServerPlayNetworking.send(player, new GatewaysNetworking.FlashPayload(fromUp));
                    ServerPlayNetworking.send(player, new GatewaysNetworking.FlashPayload(toUp));
                    checked.add(player);
                } else {
                    fromEntity.setRemoved(Entity.RemovalReason.DISCARDED);
                }
            }));

            Random rand = world.getRandom();
            int sideLength = fromBlockBox.getBlockCountX() - 1;
            int padCount = (int) Math.pow(sideLength, 2);
            int perHalf = sideLength / 2;

            int countBroken = rand.nextBetween(padCount / 8, padCount / 4);

            for (int j = 0; j < countBroken; j++) {
                int x = rand.nextBetween(-perHalf, perHalf);
                int z = rand.nextBetween(-perHalf, perHalf);
//                    debug(world, );


                if (x == 0 && z == 0) continue;

                BlockPos offsetPos = pos.add(x, 0, z);
                BlockState offsetState = world.getBlockState(offsetPos);
                Block offsetBlock = offsetState.getBlock();

                if (offsetBlock.equals(Blocks.AMETHYST_BLOCK)) {
                    world.setBlockState(offsetPos, GatewaysBlocks.CRACKED_AMETHYST.getDefaultState());
                } else if (offsetBlock.equals(GatewaysBlocks.RESONANT_AMETHYST)) {
                    world.setBlockState(offsetPos, GatewaysBlocks.CRACKED_RESONANT_AMETHYST.getDefaultState());
                }

            }
            world.playSound(
                    null,
                    pos,
                    Gateways.GATEWAY_SOUND_EVENT,
                    SoundCategory.BLOCKS,
                    1f,
                    1f
            );
            world.playSound(
                    null,
                    tunedTo,
                    Gateways.GATEWAY_SOUND_EVENT,
                    SoundCategory.BLOCKS,
                    1f,
                    1f
            );

            BlockPos fromUp = pos.up();
            BlockPos toUp = tunedTo.up();

            ServerWorld serverWorld = (ServerWorld) world;

            for (BlockPos currentPos : List.of(fromUp, toUp)) {
                for(int j = 0; j < 5; j++) {
                    BlockPos newPos = currentPos.add(
                            rand.nextBetween(-perHalf, perHalf + 1),
                            rand.nextBetween(1, sideLength),
                            rand.nextBetween(-perHalf, perHalf + 1)
                    );

                    serverWorld.spawnParticles(
                            ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                            newPos.getX() + rand.nextDouble() - 0.5,
                            newPos.getY() + rand.nextDouble() - 0.5,
                            newPos.getZ() + rand.nextDouble() - 0.5,
                            1,
                            0,
                            0,
                            0,
                            0
                    );
                }

                for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, currentPos)) {
                    if(checked.contains(player)) continue;
                    ServerPlayNetworking.send(player, new GatewaysNetworking.FlashPayload(currentPos));
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    /**
     * Finds the largest valid gateway pad for this block. Returns the side box of the teleport area, or null if none.
     * If allowCargo is true, it will allow a cube above the teleport pad where blocks can be placed. Otherwise, they will be treated as obstructive
     */
    public BlockBox validateGatewayPad(World world, BlockPos pos, boolean allowCargo) {


        // 3 --> {-1, 0, 1}
        // 5 --> {-2, -1, 0, 1, 2}

        // pattern:
        // (-1, -1)
        // (0, -1)
        // (1, -1)
        // (0, 0)
        // (1, 0)
        // (-1, 1)
        // (0, 1)
        // (1, 1)
            int sideLength = 3;
            int perSide;


            Function<BlockPos, Boolean> check = (BlockPos currentPos) -> {

                Block block = world.getBlockState(currentPos).getBlock();
                return !(block.equals(Blocks.AMETHYST_BLOCK) || block.equals(GatewaysBlocks.RESONANT_AMETHYST));
            };


            boolean failed = false;

            while (sideLength < MAX_PAD_SIDE_LENGTH) {


                perSide = sideLength / 2;
                // stage 0 is checking the pad is filled in, stage 1 is checking if there's nothing above

                for (int x = -perSide; x < perSide + 1; x++) {
                    if (x == -perSide || x == perSide) {
                        for (int z = -perSide; z < perSide + 1; z++) {
                            failed = check.apply(pos.add(x, 0, z));
                            if (failed) break;
                        }
                    } else {
                        BlockPos minPos = pos.add(x, 0, -perSide);
                        BlockPos maxPos = pos.add(x, 0, perSide);

                        failed = check.apply(minPos) || check.apply(maxPos);
                        // this is really ugly code but the other option is like 8 if statements so fine
                    }

                    if (failed) break;
                }

                if (failed) break;
                // maybe this is useless but apparently java only breaks from one loop at a time????
                // i did not know about that man



                sideLength += 2;
            }

            sideLength -= 2;
            Vec3d center = pos.toCenterPos();
            Vec3d corner1 = center.add(-sideLength / 2., 1, -sideLength / 2.);
            Vec3d corner2 = center.add(sideLength / 2., sideLength + 1, sideLength / 2.);

//            debug(corner1)
            BlockBox box = new BlockBox(
                    (int) Math.floor(corner1.x),
                    (int) Math.floor(corner1.y),
                    (int) Math.floor(corner1.z),
                    (int) Math.floor(corner2.x),
                    (int) Math.floor(corner2.y),
                    (int) Math.floor(corner2.z)
            );

            if (box.getBlockCountX() < 3) return null;


            if(!allowCargo) {
                for (int x = box.getMinX(); x < box.getMaxX(); x++) {
                    for (int y = box.getMinY(); y < box.getMaxY(); y++) {
                        for (int z = box.getMinZ(); z < box.getMaxZ(); z++) {
                            if (!world.getBlockState(new BlockPos(x, y, z)).isAir()) {
                                return null;
                            }
                        }
                    }
                }
            }
            return box;
    }


    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(GatewayHubBlock::new);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
}