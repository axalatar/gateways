package com.kingdre.gateways;

import com.kingdre.gateways.block.GatewaysBlocks;
import com.kingdre.gateways.block.entity.GatewaysBlockEntities;
import com.kingdre.gateways.item.GatewaysItemGroups;
import com.kingdre.gateways.item.GatewaysItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gateways implements ModInitializer {

    public static final String MOD_ID = "gateways";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        GatewaysItemGroups.registerItemGroups();
        GatewaysBlockEntities.registerBlockEntities();
        GatewaysBlocks.registerBlocks();
        GatewaysItems.registerItems();
    }
}
