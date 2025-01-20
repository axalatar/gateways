package com.kingdre.gateways;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.kingdre.gateways.Gateways.MOD_ID;

public class GatewaysParticles {

    public static final SimpleParticleType FLASH_PARTICLE = FabricParticleTypes.simple();


    public static void registerParticles() {
        Registry.register(
                Registries.PARTICLE_TYPE,
                Identifier.of(MOD_ID, "flash_particle"),
                FLASH_PARTICLE
        );
        Gateways.LOGGER.info("Registering components");
    }
}
