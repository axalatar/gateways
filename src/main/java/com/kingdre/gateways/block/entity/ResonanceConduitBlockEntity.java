package com.kingdre.gateways.block.entity;

import com.kingdre.gateways.Gateways;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ResonanceConduitBlockEntity extends BlockEntity {

    public ItemStack heldItem = ItemStack.EMPTY;

    public ResonanceConduitBlockEntity(BlockPos pos, BlockState state) {
        super(GatewaysBlockEntities.RESONANCE_CONDUIT_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        NbtElement itemNbt = new NbtCompound();
        if(this.heldItem != ItemStack.EMPTY) {
            itemNbt = this.heldItem.encode(wrapperLookup, itemNbt);
        }
        nbt.put("Item", itemNbt);
        super.writeNbt(nbt, wrapperLookup);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        super.readNbt(nbt, wrapperLookup);

        ItemStack item = ItemStack.EMPTY;
        NbtCompound itemNbt = (NbtCompound) nbt.get("Item");

        if(itemNbt.getSize() > 0) {
            Optional<ItemStack> maybeItem = ItemStack.fromNbt(wrapperLookup, nbt.get("Item"));
            item = maybeItem.orElse(ItemStack.EMPTY);
        }
        this.heldItem = item;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup wrapperLookup) {
        return createNbt(wrapperLookup);
    }
}