package com.kingdre.gateways.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class CrackedResonantAmethystBlock extends AbstractCrackedBlock {
    public CrackedResonantAmethystBlock(Settings settings) {
        super(settings);
    }

    @Override
    BlockState getFixedBlock() {
        return GatewaysBlocks.RESONANT_AMETHYST.getDefaultState();
    }
}
