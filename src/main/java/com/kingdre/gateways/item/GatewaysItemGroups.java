package com.kingdre.gateways.item;

import com.kingdre.gateways.Gateways;
import com.kingdre.gateways.block.GatewaysBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GatewaysItemGroups {
    public static final ItemGroup GATEWAYS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Gateways.MOD_ID, "gateways"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.gateways"))
                    .icon(
                            () -> new ItemStack(GatewaysItems.TUNING_FORK))
                    .entries((displayContext, entries) -> {

                        entries.add(GatewaysItems.TUNING_FORK);
                        entries.add(GatewaysBlocks.GATEWAY_HUB);
                        entries.add(GatewaysBlocks.RESONANT_AMETHYST);
                        entries.add(GatewaysBlocks.RESONANCE_CONDUIT);
                    })
                    .build()
    );

    public static void registerItemGroups() {
        Gateways.LOGGER.info("Registering item groups");
    }
}