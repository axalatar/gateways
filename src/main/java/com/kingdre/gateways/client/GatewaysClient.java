package com.kingdre.gateways.client;

import com.kingdre.gateways.Gateways;
import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class GatewaysClient implements ClientModInitializer {

    boolean shader = false;

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(
                GatewaysBlockEntities.RESONANCE_CONDUIT_BLOCK_ENTITY,
                ResonanceConduitRenderer::new
        );
//        MinecraftClient.getInstance().gameRenderer.loadPostProcessor(Identifier.of("gateways", "shaders/post/test.json"));

        WorldRenderEvents.END.register(context -> {
            if (!shader) {
                context.gameRenderer().loadPostProcessor(Identifier.of("gateways", "shaders/post/test.json"));
//                context.gameRenderer().reset();
                shader = true;
            }
                //                MinecraftClient client = MinecraftClient.getInstance();
//                try {
//                    shader = new PostEffectProcessor(
//                            client.getTextureManager(),
//                            client.getResourceManager(),
//                            client.getFramebuffer(),
//                            new Identifier("gateways", "shaders/post/my_shader.json")
//                    );
//                } catch (IOException e) {
//                    Gateways.LOGGER.error(e.toString());
//                }
//            }
        });
    }
}
