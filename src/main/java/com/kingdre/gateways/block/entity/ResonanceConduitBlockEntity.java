package com.kingdre.gateways.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResonanceConduitBlockEntity extends BlockEntity {

    public ItemStack heldItem = ItemStack.EMPTY;

    public ResonanceConduitBlockEntity(BlockPos pos, BlockState state) {
        super(GatewaysBlockEntities.RESONANCE_CONDUIT_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtCompound itemNbt = new NbtCompound();
        this.heldItem.writeNbt(itemNbt);
        nbt.put("Item", itemNbt);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.heldItem = ItemStack.fromNbt((NbtCompound) nbt.get("Item"));
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