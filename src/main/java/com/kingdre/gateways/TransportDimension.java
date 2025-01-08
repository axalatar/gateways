package com.kingdre.gateways;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.OptionalLong;

public class TransportDimension {
    public static final RegistryKey<DimensionOptions> TRANSPORT_DIM_KEY = RegistryKey.of(RegistryKeys.DIMENSION,
            new Identifier(Gateways.MOD_ID, "transport"));
    public static final RegistryKey<World> TRANSPORT_LEVEL_KEY = RegistryKey.of(RegistryKeys.WORLD,
            new Identifier(Gateways.MOD_ID, "transport"));
    public static final RegistryKey<DimensionType> TRANSPORT_DIM_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            new Identifier(Gateways.MOD_ID, "transport_type"));

    public static void bootstrapType(Registerable<DimensionType> context) {
        context.register(TRANSPORT_DIM_TYPE, new DimensionType(
                OptionalLong.of(1),
                true,
                false,
                false,
                false,
                1.0,
                false,
                false,
                0,
                64,
                64,
                BlockTags.INFINIBURN_OVERWORLD,
                DimensionTypes.THE_END_ID,
                1.0f,
                new DimensionType.MonsterSettings(false, false, UniformIntProvider.create(0, 0), 0)));
    }

    public static void initializeDimension() {
//        Registry.register(Registry.CHUNK_GENERATOR, new Identifier("fabric_dimension", "void"), VoidChunkGenerator.CODEC);

//        WORLD_KEY = RegistryKey.of(Registry.DIMENSION, new Identifier("fabric_dimension", "void"));
    }
}
