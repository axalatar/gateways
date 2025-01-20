package com.kingdre.gateways;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.stream.IntStream;

public class GatewaysComponents {

    public record FrequencyComponent(int x, int y, int z) {}

    public static final Codec<FrequencyComponent> FREQUENCY_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("x").forGetter(FrequencyComponent::x),
            Codec.INT.fieldOf("y").forGetter(FrequencyComponent::y),
            Codec.INT.fieldOf("z").forGetter(FrequencyComponent::z)
            ).apply(builder, FrequencyComponent::new));

    public static final ComponentType<FrequencyComponent> FREQUENCY = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Gateways.MOD_ID, "frequency"),
            ComponentType.<FrequencyComponent>builder().codec(FREQUENCY_CODEC).build()
    ); // this is probably the only component, but it looks nicer if it has its own file

    public static void registerComponents() {
        Gateways.LOGGER.info("Registering components");
    }
}
