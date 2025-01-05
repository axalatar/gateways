package com.kingdre.gateways.block.entity;

import com.kingdre.gateways.Gateways;
import com.kingdre.gateways.block.GatewaysBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class GatewaysBlockEntities {

public static final BlockEntityType<GatewayHubBlockEntity> GATEWAY_HUB_BLOCK_ENTITY =
        registerBlockEntity(
                "gateway_hub",
                GatewayHubBlockEntity::new,
                GatewaysBlocks.GATEWAY_HUB
);



    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, FabricBlockEntityTypeBuilder.Factory<T> blockEntitySupplier, Block block) {
        return Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(Gateways.MOD_ID, name),
                FabricBlockEntityTypeBuilder.create(blockEntitySupplier, block).build());
    }


    public static void registerBlockEntities() {
        Gateways.LOGGER.info("Registering block entities");
    }
}
