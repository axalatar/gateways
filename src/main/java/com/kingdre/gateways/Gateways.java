package com.kingdre.gateways;

import com.kingdre.gateways.block.GatewaysBlocks;
import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
import com.kingdre.gateways.item.GatewaysItemGroups;
import com.kingdre.gateways.item.GatewaysItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gateways implements ModInitializer {

    public static final String MOD_ID = "gateways";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    public static final Identifier GATEWAY_SOUND_ID = Identifier.of("gateways", "gateway_teleport");
    public static SoundEvent GATEWAY_SOUND_EVENT = SoundEvent.of(GATEWAY_SOUND_ID);


    @Override
    public void onInitialize() {
        Registry.register(Registries.SOUND_EVENT, GATEWAY_SOUND_ID, GATEWAY_SOUND_EVENT);

        GatewaysItemGroups.registerItemGroups();
        GatewaysBlockEntities.registerBlockEntities();
        GatewaysBlocks.registerBlocks();
        GatewaysItems.registerItems();
    }
}
