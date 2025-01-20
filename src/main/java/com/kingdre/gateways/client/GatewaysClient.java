package com.kingdre.gateways.client;

import com.kingdre.gateways.Gateways;
import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
//import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.AreaLight;
import foundry.veil.api.client.render.light.PointLight;
import foundry.veil.api.event.VeilPostProcessingEvent;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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
import net.minecraft.util.math.BlockPos;

import java.io.IOException;

public class GatewaysClient implements ClientModInitializer {

    public static BlockPos pos = null;
    public static int tick = 0;

    private static PointLight light = null;

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(
                GatewaysBlockEntities.RESONANCE_CONDUIT_BLOCK_ENTITY,
                ResonanceConduitRenderer::new
        );

        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if(light == null) {
                light = new PointLight().setColor(255, 255, 255);
                VeilRenderSystem.renderer().getLightRenderer().addLight(light);
            }
            if(pos != null) {

                light
                        .setPosition(pos.getX(), pos.getY()+1, pos.getZ())
                        .setBrightness(30)
                        .setRadius(Math.max(0f, 10f - tick));
                tick++;
            }
        });
//        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer()
    }
}