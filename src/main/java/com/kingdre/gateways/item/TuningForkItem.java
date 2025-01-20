package com.kingdre.gateways.item;

import com.kingdre.gateways.Gateways;
import com.kingdre.gateways.GatewaysComponents;
import com.kingdre.gateways.block.GatewaysBlocks;
import com.kingdre.gateways.block.entity.GatewayHubBlockEntity;
import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.AreaLight;
import foundry.veil.api.client.render.light.PointLight;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class TuningForkItem extends Item {

    final static private int MAX_RESONANT_CRYSTAL_DISTANCE = 10;

    public TuningForkItem() {
        super(new Settings().maxCount(1));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient()) {
//            context.getWorld().getServer().getWorld(TransportDimension.TRANSPORT_LEVEL_KEY);

            PlayerEntity player = context.getPlayer();
            ItemStack heldItem = context.getStack();
            BlockPos pos = context.getBlockPos();
            World world = context.getWorld();
            Block block = world.getBlockState(pos).getBlock();

//            ComponentMap components = heldItem.getComponents();
            if (block.equals(GatewaysBlocks.GATEWAY_HUB) || block.equals(GatewaysBlocks.RESONANT_AMETHYST)) {

                if (player.isSneaking() && block.equals(GatewaysBlocks.GATEWAY_HUB)) {
                    // if the player is sneaking, take the frequency
//                    heldItem.applyComponentsFrom(ComponentMap.);
                    heldItem.set(GatewaysComponents.FREQUENCY, new GatewaysComponents.FrequencyComponent(
                            pos.getX(),
                            pos.getY(),
                            pos.getZ()
                    ));

                } else {
                    // otherwise, give the frequency
                    GatewaysComponents.FrequencyComponent frequency = heldItem.get(GatewaysComponents.FREQUENCY);
                    if(frequency == null) return ActionResult.PASS;
                    giveFrequency(pos, world, heldItem.get(GatewaysComponents.FREQUENCY));
                }
                world.playSound(
                        null,
                        pos,
                        SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                        SoundCategory.BLOCKS,
                        50f,
                        1f
                );

                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.contains(GatewaysComponents.FREQUENCY);
    }


    /**
     * Gets the frequency component from nbt, and uses it on the given hub or hub connected through resonant amethyst
     */
    public static void giveFrequency(BlockPos pos, World world, GatewaysComponents.FrequencyComponent frequencyComponent) {

        if(frequencyComponent == null) return;
        Block block = world.getBlockState(pos).getBlock();
        if (block.equals(GatewaysBlocks.GATEWAY_HUB)) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity == null || !entity.getType().equals(GatewaysBlockEntities.GATEWAY_HUB_BLOCK_ENTITY))
                return;

            GatewayHubBlockEntity hubEntity = (GatewayHubBlockEntity) entity;

            List<Integer> frequency = List.of(frequencyComponent.x(), frequencyComponent.y(), frequencyComponent.z());

            BlockPos entityPos = hubEntity.getPos();
            if (
                    frequency.get(0) == entityPos.getX()
                            && frequency.get(1) == entityPos.getY()
                            && frequency.get(2) == entityPos.getZ())
                return;

            hubEntity.heldFrequency = frequency;
            hubEntity.markDirty();
        } else if (block.equals(GatewaysBlocks.RESONANT_AMETHYST)) {

            List<Pair<BlockPos, Integer>> blocks = new ArrayList<>(List.of(new Pair<>(pos, 0)));
            List<BlockPos> foundHubs = new ArrayList<>();

            for (Direction direction : Direction.values()) {
                for (int i = 0; i < blocks.size(); i++) {
                    Pair<BlockPos, Integer> currentPos = blocks.get(i);

                    BlockPos offset = currentPos.getLeft().offset(direction);
                    Block offsetBlock = world.getBlockState(offset).getBlock();
                    if (offsetBlock.equals(GatewaysBlocks.RESONANT_AMETHYST) || offsetBlock.equals(GatewaysBlocks.GATEWAY_HUB)) {
                        if (currentPos.getRight() < MAX_RESONANT_CRYSTAL_DISTANCE) {

                            if (blocks.stream().anyMatch(pair -> pair.getLeft().equals(offset))) continue;

                            blocks.add(new Pair<>(offset, currentPos.getRight() + 1));
                            if (offsetBlock.equals(GatewaysBlocks.GATEWAY_HUB)) foundHubs.add(offset);
                        }
                    }
                }
            }

            foundHubs.forEach(position -> giveFrequency(position, world, frequencyComponent));
        }
    }
}
