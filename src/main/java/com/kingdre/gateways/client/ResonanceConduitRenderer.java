package com.kingdre.gateways.client;

import com.kingdre.gateways.Gateways;
import com.kingdre.gateways.block.entity.ResonanceConduitBlockEntity;
import com.kingdre.gateways.item.GatewaysItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class ResonanceConduitRenderer implements BlockEntityRenderer<ResonanceConduitBlockEntity> {

    public ResonanceConduitRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(ResonanceConduitBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // from https://wiki.fabricmc.net/tutorial:blockentityrenderers
        ItemStack held = entity.heldItem;

        matrices.push();
        double offset = Math.sin((entity.getWorld().getTime() + tickDelta) / 8.0) / 16.0;
        matrices.translate(0.5, 1.25 + offset, 0.5);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((entity.getWorld().getTime() + tickDelta) * 4));
        int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
        MinecraftClient.getInstance().getItemRenderer().renderItem(held, ModelTransformationMode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);

        matrices.pop();
    }
}
