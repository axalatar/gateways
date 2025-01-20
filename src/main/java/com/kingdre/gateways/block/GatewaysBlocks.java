package com.kingdre.gateways.block;

import com.kingdre.gateways.Gateways;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class GatewaysBlocks {
    public static final Block GATEWAY_HUB = registerBlock("gateway_hub", new GatewayHubBlock(AbstractBlock.Settings.copy(Blocks.AMETHYST_BLOCK).strength(10f)));
    public static final Block RESONANCE_CONDUIT = registerBlock("resonance_conduit", new ResonanceConduitBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_BLACKSTONE)));

    public static final Block RESONANT_AMETHYST = registerBlock("resonant_amethyst", new Block(AbstractBlock.Settings.copy(Blocks.AMETHYST_BLOCK)));

    public static final Block CRACKED_AMETHYST = registerBlock("cracked_amethyst", new CrackedAmethystBlock(AbstractBlock.Settings.copy(Blocks.AMETHYST_BLOCK)));
    public static final Block CRACKED_RESONANT_AMETHYST = registerBlock("cracked_resonant_amethyst", new CrackedResonantAmethystBlock(AbstractBlock.Settings.copy(Blocks.AMETHYST_BLOCK)));


    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Gateways.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, Identifier.of(Gateways.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerBlocks() {
        Gateways.LOGGER.info("Registering blocks");
    }
}
