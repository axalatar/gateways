package com.kingdre.gateways.block;

import com.kingdre.gateways.Gateways;
import com.kingdre.gateways.block.entity.GatewayHubBlockEntity;
import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
import com.kingdre.gateways.item.GatewaysItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.StructureBlockBlockEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class GatewayHubBlock extends BlockWithEntity {

    private final static int MAX_PAD_SIDE_LENGTH = 50;
    // probably a good idea to have this
    // is there a better way to do a const in java?

    protected GatewayHubBlock() {
        super(Settings.copy(Blocks.AMETHYST_BLOCK).strength(10f));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GatewayHubBlockEntity(pos, state);
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            if (!player.getActiveHand().equals(hand)) return ActionResult.PASS;

            BlockEntity entity = world.getBlockEntity(pos);
            if (entity == null || !entity.getType().equals(GatewaysBlockEntities.GATEWAY_HUB_BLOCK_ENTITY))
                return ActionResult.PASS;

            GatewayHubBlockEntity hubEntity = (GatewayHubBlockEntity) entity;

            List<Integer> frequency = hubEntity.heldFrequency;
            if (frequency.isEmpty()) return ActionResult.PASS;

//            player.teleport(frequency.get(0), frequency.get(1), frequency.get(2));
            player.sendMessage(Text.of(String.valueOf(validateGatewayPad(state, world, pos, true))));
        }
        return ActionResult.PASS;
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
     * Finds the largest valid gateway pad for this block. Returns the bounding box of the teleport area, or null if none.
     * If updateState is true, will update the blockstate of the hub depending on whether there's a valid pad
     */
    public Box validateGatewayPad(BlockState state, World world, BlockPos pos, boolean updateState) {
        int sideLength = 3;
        int perSide = sideLength / 2;

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

        boolean failed = false;

        while (sideLength < MAX_PAD_SIDE_LENGTH) {

            perSide = sideLength / 2;
            for (int stage = 0; stage < 2; stage++) {
                // stage 0 is checking the pad is filled in, stage 1 is checking if there's nothing above

                Function<BlockPos, Boolean> check =
                        stage == 0 ?
                                (BlockPos currentPos) -> !world.getBlockState(currentPos).getBlock().equals(Blocks.AMETHYST_BLOCK) :
                                (BlockPos currentPos) -> !isHighestBlock(world, currentPos);

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

            }
            if (failed) break;

            sideLength += 2;
        }
        if (failed && sideLength == 3) return null;

        sideLength -= 2;
//        StructureBlockBlockEntityRenderer

        return Box.of(pos.up(perSide).toCenterPos(), sideLength, sideLength, sideLength);
    }
}