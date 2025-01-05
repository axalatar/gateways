package com.kingdre.gateways.item;

import com.kingdre.gateways.Gateways;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class GatewaysItems {
    public static final Item TUNING_FORK = registerItem("tuning_fork", new TuningForkItem());


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Gateways.MOD_ID, name), item);
    }

    public static void registerItems() {
        Gateways.LOGGER.info("Registering items");
        // add to creative tab, do stuff to items, etc.
    }
}
