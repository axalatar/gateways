package com.kingdre.gateways.block;

import com.kingdre.gateways.block.entity.GatewayHubBlockEntity;
import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.PlaceCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GatewayHubBlock extends BlockWithEntity {
    private static final BooleanProperty POWERED = Properties.POWERED;
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty OPEN = BooleanProperty.of("open");


    private final static int MAX_PAD_SIDE_LENGTH = 51;
    // must be an odd number
    // probably a good idea to have this
    // is there a better way to do a const in java?

    protected GatewayHubBlock() {
        super(Settings.copy(Blocks.AMETHYST_BLOCK).strength(10f));
        this.setDefaultState(
                this.stateManager.getDefaultState().with(POWERED, false).with(FACING, Direction.NORTH).with(OPEN, false)
        );

    }
    /*
    GRAPHICS CODE:
    TODO render beam on teleport
    TODO render skybox line
    TODO render skybox circles
    TODO particles on teleport
    TODO screenshake and flash

    NON-GRAPHICS CODE:
    TODO async block checking
    TODO explosion on teleporting blocks into each other
    TODO teleportation cost
    TODO fix teleporting outside pad
    TODO explode on going up into blocks
    TODO automatically open/close on pad completed/destroyed

    OTHER:
    TODO sounds
    TODO recipes
    TODO resonance conduit model

    PRESENTATION:
    TODO testing
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            if (!player.getActiveHand().equals(hand) || !player.getStackInHand(hand).isEmpty())
                return ActionResult.PASS;

            return attemptTeleport(state, world, pos);
        }
        return ActionResult.PASS;
    }



    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if(world.isClient()) return;

        boolean powered = world.isReceivingRedstonePower(pos);
        boolean alreadyPowered = state.get(POWERED);
        if (!alreadyPowered) {
            if (powered) {
                this.attemptTeleport(state, world, pos);
                world.setBlockState(pos, state.with(POWERED, true), Block.NO_REDRAW);
            }
        }
        else if (!powered) world.setBlockState(pos, state.with(POWERED, false));
    }

    private void debug(World world, String message) {
        world.getPlayers().forEach((playerEntity -> {
            playerEntity.sendMessage(Text.of(message));
        }));
    }


    private boolean isHighestBlock(World world, BlockPos pos) {
        for (int y = pos.getY() + 1; y < world.getTopY(); y++) {
            if (!world.getBlockState(pos.withY(y)).isAir()) return false;
//            world.getBlockState(pos.withY(y));
        }
        return true;
    }

    /**
     * Attempt to teleport blocks and entities from the gateway, validates the gateway before teleportation.
     */
    public ActionResult attemptTeleport(BlockState state, World world, BlockPos pos) {
        if (!world.isClient()) {


            BlockEntity entity = world.getBlockEntity(pos);
            if (entity == null || !entity.getType().equals(GatewaysBlockEntities.GATEWAY_HUB_BLOCK_ENTITY))
                return ActionResult.PASS;

            GatewayHubBlockEntity hubEntity = (GatewayHubBlockEntity) entity;

            List<Integer> frequency = hubEntity.heldFrequency;
            if (frequency.isEmpty()) return ActionResult.PASS;

            boolean open = state.get(OPEN);
            BlockBox fromBox = validateGatewayPad(state, world, pos, true, true);

            debug(world, String.valueOf(open));
            if (fromBox == null) return ActionResult.FAIL;
            if(!open) return ActionResult.SUCCESS;

            BlockPos tunedTo = BlockPos.ofFloored(frequency.get(0), frequency.get(1), frequency.get(2));
            debug(world, String.valueOf(tunedTo));
            BlockEntity destinationEntity = world.getBlockEntity(tunedTo);

            if (destinationEntity == null || !destinationEntity.getType().equals(GatewaysBlockEntities.GATEWAY_HUB_BLOCK_ENTITY))
                return ActionResult.FAIL;

//            ((ServerWorld) world).getChunkManager().getChunk(0, 0)

            BlockBox toBox = validateGatewayPad(world.getBlockState(tunedTo), world, tunedTo, true, false);
            debug(world, String.valueOf(toBox));
            if (fromBox.intersects(toBox)) return ActionResult.FAIL;


            if (fromBox.getBlockCountX() <= toBox.getBlockCountX()) {

                StructureTemplate template = new StructureTemplate();
                template.saveFromWorld(
                        world,
                        BlockPos.ofFloored(fromBox.getMinX(), fromBox.getMinY(), fromBox.getMinZ()),
                        fromBox.getDimensions(),
                        true,
                        null
                );

                template.place(
                        (ServerWorld) world,
                        BlockPos.ofFloored(toBox.getMinX(), toBox.getMinY(), toBox.getMinZ()),
                        BlockPos.ofFloored(toBox.getMinX(), toBox.getMinY(), toBox.getMinZ()),
                        new StructurePlacementData(),
                        world.getRandom(),
                        Block.NOTIFY_ALL
                );

                CuboidBlockIterator iterator = new CuboidBlockIterator(
                        fromBox.getMinX(),
                        fromBox.getMinY(),
                        fromBox.getMinZ(),
                        fromBox.getMaxX(),
                        fromBox.getMaxY(),
                        fromBox.getMaxZ()
                );

                int i = 0;
                // putting the extra i here because i don't trust the block iterator enough

                while (iterator.step()) {
                    if(i > Math.pow(MAX_PAD_SIDE_LENGTH, 2)) break; // just to be safe

                    BlockPos fromPos = BlockPos.ofFloored(iterator.getX(), iterator.getY(), iterator.getZ());

                    BlockEntity fromEntity = world.getBlockEntity(fromPos);

                    if(fromEntity != null)
                        fromEntity.markRemoved();

                    world.setBlockState(
                            new BlockPos(
                                    iterator.getX(),
                                    iterator.getY(),
                                    iterator.getZ()
                            ),
                            Blocks.AIR.getDefaultState()
                    );
                    i++;
                }

                Vec3d difference = new Vec3d(toBox.getMinX(), toBox.getMinY(), toBox.getMinZ())
                        .subtract(fromBox.getMinX(), fromBox.getMinY(), fromBox.getMinZ());

                world.getEntitiesByType(TypeFilter.instanceOf(Entity.class), Box.from(fromBox).withMaxY(1000), o -> true).forEach((fromEntity -> {
                    if(fromEntity.getType() == EntityType.PLAYER) {
                        fromEntity.requestTeleportOffset(difference.getX(), difference.getY(), difference.getZ());
                    }
                    else {
                        fromEntity.remove(Entity.RemovalReason.DISCARDED);
                    }
                }));


                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    /**
     * Finds the largest valid gateway pad for this block. Returns the bounding box of the teleport area, or null if none.
     * If updateState is true, will update the blockstate of the hub depending on whether there's a valid pad.
     * If allowCargo is true, it will allow a cube above the teleport pad where blocks can be placed. Otherwise, they will be treated as obstructive.
     */
    public BlockBox validateGatewayPad(BlockState state, World world, BlockPos pos, boolean updateState, boolean allowCargo) {
        int sideLength = 3;
        int perSide;

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

        Function<BlockPos, Boolean> check = (BlockPos currentPos) -> {
            Block block = world.getBlockState(currentPos).getBlock();
            return !(block.equals(Blocks.AMETHYST_BLOCK) || block.equals(GatewaysBlocks.RESONANT_AMETHYST));
        };

//        debug(world, String.valueOf(check.apply(new BlockPos(1000, 1000, 1000))));


        boolean failed = false;

        while (sideLength < MAX_PAD_SIDE_LENGTH) {


            perSide = sideLength / 2;
            for (int stage = 0; stage < 2; stage++) {
                // stage 0 is checking the pad is filled in, stage 1 is checking if there's nothing above

                for (int x = -perSide; x < perSide + 1; x++) {
                    if (stage == 0) {
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
                    } else {
                        if(x == -perSide || x == perSide) {
                            for (int z = -perSide; z < perSide + 1; z++) {
                                if(allowCargo) {
                                    failed = !isHighestBlock(world, pos.add(x, sideLength, z));
                                }
                                else failed = !isHighestBlock(world, pos.add(x, 0, z));                                if (failed) break;
                            }
                        }
                        for (int z = -perSide + 1; z < perSide; z++) {
                            if(allowCargo) {
                                failed = !world.getBlockState(pos.add(x, sideLength - 1, z))
                                        .equals(Blocks.AIR.getDefaultState());
                            }
                            if (failed) break;
                        }
                    }
                    if (failed) break;
                }

                if (failed) break;
                // maybe this is useless but apparently java only breaks from one loop at a time????
                // i did not know about that man

            }
            if (failed) break;

            sideLength += 2;
        }
        if (failed && sideLength == 3) {
            if(updateState) world.setBlockState(pos, state.with(OPEN, false));
            return null;
        }

        sideLength -= 2;

//        Vec3d center = pos.up(perSide).toCenterPos();

//        return BlockBox.create(
//                new Vec3i(
//                        (int) (center.x - sideLength / 2.0),
//                        (int) (center.y - sideLength / 2.0),
//                        (int) (center.z - sideLength / 2.0)),
//                new Vec3i(
//                        (int) (center.x + sideLength / 2.0),
//                        (int) (center.y + sideLength / 2.0),
//                        (int) (center.z + sideLength / 2.0)
//                ));
        Vec3d center = pos.toCenterPos();
        Vec3d corner1 = center.add(-sideLength / 2., 1, -sideLength / 2.);
        Vec3d corner2 = center.add(sideLength / 2., sideLength + 1, sideLength / 2.);

        if(updateState) world.setBlockState(pos, state.with(OPEN, true));
        debug(world, String.valueOf(updateState));
        return new BlockBox(
                (int) corner1.x,
                (int) corner1.y,
                (int) corner1.z,
                (int) corner2.x,
                (int) corner2.y,
                (int) corner2.z
        );
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