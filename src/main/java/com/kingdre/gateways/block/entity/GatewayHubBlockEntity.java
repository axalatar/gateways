package com.kingdre.gateways.block.entity;

import com.kingdre.gateways.Gateways;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.FabricUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GatewayHubBlockEntity extends BlockEntity {

    public List<Integer> heldFrequency = new ArrayList<>();

    public boolean currentlyTransporting = false;
    public StructureTemplate transportingStructure;
    public BlockPos transportingDestination;

    public GatewayHubBlockEntity(BlockPos pos, BlockState state) {
        super(GatewaysBlockEntities.GATEWAY_HUB_BLOCK_ENTITY, pos, state);
    }

    public void setTransporting(StructureTemplate template, BlockPos destination) {
        this.transportingStructure = template;
        this.transportingDestination = destination;
        this.currentlyTransporting = true;

        this.world.getPlayers().forEach((playerEntity -> {
            playerEntity.sendMessage(Text.of("start"));
        }));

        this.world.scheduleBlockTick(this.pos, this.getCachedState().getBlock(), 60);
        this.markDirty();
     //   this.world.getServer().
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putIntArray("heldFrequency", this.heldFrequency);
        if(this.transportingDestination != null) {
            nbt.putIntArray("currentDestination", List.of(
                    this.transportingDestination.getX(),
                    this.transportingDestination.getY(),
                    this.transportingDestination.getZ()
            ));
        }
        if(this.transportingStructure != null) {
            this.transportingStructure.writeNbt(nbt);
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Gateways.LOGGER.info(String.valueOf(this.world.isClient()));
        super.readNbt(nbt);
        this.heldFrequency = Arrays.stream(nbt.getIntArray("heldFrequency")).boxed().toList();
        int[] currentDestination = nbt.getIntArray("currentDestination");
        if(currentDestination.length >= 3) {
            this.transportingDestination = new BlockPos(currentDestination[0], currentDestination[1], currentDestination[2]);
        }
//        Gateways.LOGGER.info("TEST TEST TEST");
//        Gateways.LOGGER.info(String.valueOf(this.world));
//        Gateways.LOGGER.info(String.valueOf(this.world.isClient()));

//        this.world.getPlayers().forEach((playerEntity -> playerEntity.sendMessage(Text.of(String.valueOf(this.world != null && !this.world.isClient())))));

        if(this.world != null && !this.world.isClient()) {
            MinecraftServer server = ((ServerWorld) this.world).getServer();
//            server.getPlayerManager().getPlayerList().forEach((player -> player.sendMessage(Text.of("place test"))));

//                ServerWorld
//                MinecraftServer.
            this.world.getPlayers().forEach((playerEntity -> playerEntity.sendMessage(Text.of("aaa"))));
            this.transportingStructure = server.getStructureTemplateManager().createTemplate(nbt);
        }
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
