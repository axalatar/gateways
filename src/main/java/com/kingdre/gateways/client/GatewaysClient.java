package com.kingdre.gateways.client;

import com.kingdre.gateways.GatewaysNetworking;
import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
//import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.PointLight;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class GatewaysClient implements ClientModInitializer {

    static private final List<Pair<PointLight, Integer>> lights = new ArrayList<>();

    static private final double BRIGHTNESS = 50;
    static private final double SMOOTHNESS = 0.5;
    static private final double MAX_AGE = 160;

    @Override
    public void onInitializeClient() {


        BlockEntityRendererFactories.register(
                GatewaysBlockEntities.RESONANCE_CONDUIT_BLOCK_ENTITY,
                ResonanceConduitRenderer::new
        );



        ClientPlayNetworking.registerGlobalReceiver(GatewaysNetworking.FlashPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                BlockPos pos = payload.blockPos();
                PointLight light = new PointLight()
                        .setBrightness(50)
                        .setPosition(pos.getX(), pos.getY(), pos.getZ())
                        .setRadius(15);
                VeilRenderSystem.renderer().getLightRenderer().addLight(light);
                lights.add(new Pair<>(light, 0));
            });
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {

            List<Integer> toRemove = new ArrayList<>();
//            if(!lights.isEmpty()) VeilBloomRenderer.enable();
                for(int i = 0; i < lights.size(); i++) {
                Pair<PointLight, Integer> data = lights.get(i);

                double a = BRIGHTNESS + SMOOTHNESS;
                double c = Math.log10(SMOOTHNESS / a) / -MAX_AGE;

                int age = data.getRight();

                double newBrightness = (float) (a * (Math.pow(10, -c*data.getRight())) - SMOOTHNESS);
                data.getLeft().setBrightness((float) newBrightness);
                lights.get(i).setRight(age + 1);

                if(newBrightness <= 0 || age + 1 >= MAX_AGE) {
                    toRemove.add(i);
                }
            }

            for(int i = toRemove.size() - 1; i >= 0; i--) {
                int index = toRemove.get(i);
                VeilRenderSystem.renderer().getLightRenderer().removeLight(lights.get(index).getLeft());
                lights.remove(index);
            }
        });
    }
}