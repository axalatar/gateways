package com.kingdre.gateways;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static com.kingdre.gateways.Gateways.MOD_ID;

public class GatewaysNetworking {
    public static final Identifier FLASH_PACKET_ID = Identifier.of(MOD_ID, "flash");

    public record FlashPayload(BlockPos blockPos) implements CustomPayload {
        public static final CustomPayload.Id<FlashPayload> ID = new CustomPayload.Id<>(FLASH_PACKET_ID);
        public static final PacketCodec<RegistryByteBuf, FlashPayload> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, FlashPayload::blockPos, FlashPayload::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void registerPackets() {
        Gateways.LOGGER.info("Registering packets");
        PayloadTypeRegistry.playS2C().register(GatewaysNetworking.FlashPayload.ID, GatewaysNetworking.FlashPayload.CODEC);
    }
}
