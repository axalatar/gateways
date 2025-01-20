package com.kingdre.gateways.client;

import com.kingdre.gateways.Gateways;
import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
//import foundry.veil.api.client.render.VeilRenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class GatewaysClient implements ClientModInitializer {

    static PostEffectProcessor shader;

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(
                GatewaysBlockEntities.RESONANCE_CONDUIT_BLOCK_ENTITY,
                ResonanceConduitRenderer::new
        );

//        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer()
    }
}