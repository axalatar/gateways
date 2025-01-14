package com.kingdre.gateways.block;

import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
import com.kingdre.gateways.block.entity.ResonanceConduitBlockEntity;
import com.kingdre.gateways.item.GatewaysItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SnowballItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrackedAmethystBlock extends AbstractCrackedBlock {
    public CrackedAmethystBlock(Settings settings) {
        super(settings);
    }

    @Override
    BlockState getFixedBlock() {
        return Blocks.AMETHYST_BLOCK.getDefaultState();
    }
}
