package com.kingdre.gateways.client;

import com.kingdre.gateways.Gateways;
import com.kingdre.gateways.GatewaysParticles;
import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
//import foundry.veil.api.client.render.VeilRenderSystem;
import com.kingdre.gateways.client.particle.FlashParticle;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.AreaLight;
import foundry.veil.api.client.render.light.PointLight;
import foundry.veil.api.event.VeilPostProcessingEvent;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class GatewaysClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {

        BlockEntityRendererFactories.register(
                GatewaysBlockEntities.RESONANCE_CONDUIT_BLOCK_ENTITY,
                ResonanceConduitRenderer::new
        );

        ParticleFactoryRegistry.getInstance().register(GatewaysParticles.FLASH_PARTICLE, FlashParticle.Factory::new);

//        VeilRenderSystem.renderer().getLightRenderer().
//        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> {
//            VeilRenderSystem.renderer().getLightRenderer().addLight(new PointLight().setBrightness(0));
//        });
    }
}