package com.kingdre.gateways.client;

import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class GatewaysClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(
                GatewaysBlockEntities.RESONANCE_CONDUIT_BLOCK_ENTITY,
                ResonanceConduitRenderer::new
        );
    }
}
