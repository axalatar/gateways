package com.kingdre.gateways.item;

import com.kingdre.gateways.TransportDimension;
import com.kingdre.gateways.block.GatewaysBlocks;
import com.kingdre.gateways.block.entity.GatewayHubBlockEntity;
import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class TuningForkItem extends Item {
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

            if(!context.getWorld().getBlockState(pos).getBlock().equals(GatewaysBlocks.GATEWAY_HUB)) return ActionResult.PASS;

            if (heldItem.getItem().equals(GatewaysItems.TUNING_FORK)) {

                NbtCompound nbt = heldItem.getOrCreateNbt();

                if (player.isSneaking()) {
                    // if the player is sneaking, take the frequency

//                    player.sendMessage(Text.of(String.valueOf(pos.getX())));
                    nbt.putIntArray("frequency", List.of(pos.getX(), pos.getY(), pos.getZ()));
                    heldItem.setNbt(nbt);

                } else {
                    // otherwise, give the frequency
                    BlockEntity entity = context.getWorld().getBlockEntity(pos);
                    if (entity == null || !entity.getType().equals(GatewaysBlockEntities.GATEWAY_HUB_BLOCK_ENTITY))
                        return ActionResult.FAIL;

                    GatewayHubBlockEntity hubEntity = (GatewayHubBlockEntity) entity;

                    List<Integer> frequency = Arrays.stream(nbt.getIntArray("frequency")).boxed().toList();
                    if (frequency.isEmpty()) return ActionResult.FAIL;

                    BlockPos entityPos = hubEntity.getPos();
                    if (
                            frequency.get(0) == entityPos.getX()
                                    && frequency.get(1) == entityPos.getY()
                                    && frequency.get(2) == entityPos.getZ())
                        return ActionResult.FAIL;

                    hubEntity.heldFrequency = frequency;
                    hubEntity.markDirty();
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.hasNbt();
    }
}
