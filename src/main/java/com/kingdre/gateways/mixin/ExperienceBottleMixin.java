package com.kingdre.gateways.mixin;

import com.kingdre.gateways.Gateways;
import com.kingdre.gateways.block.AbstractCrackedBlock;
import com.kingdre.gateways.item.GatewaysItems;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ExperienceBottleEntity.class)
public abstract class ExperienceBottleMixin extends Entity {


    public ExperienceBottleMixin(EntityType<?> type, World world) {
        super(type, world);
    }

        @Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;syncWorldEvent(ILnet/minecraft/util/math/BlockPos;I)V", shift = At.Shift.AFTER), cancellable = true)
    private void injected(HitResult hitResult, CallbackInfo ci) {

        List<Entity> nearbyShards = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(1, 1, 1), (entity) -> {
            if(entity instanceof ItemEntity itemEntity) {

                ItemStack item = itemEntity.getStack();
                return item.getItem().equals(Items.AMETHYST_SHARD);
            }
            return false;
        });


        if(!nearbyShards.isEmpty()) {
            ItemEntity item = ((ItemEntity) nearbyShards.get(0));
            ItemStack held = item.getStack();

            held.decrement(1);
            item.setStack(held);


            Box fixBox = this.getBoundingBox().expand(3,3,3);
            CuboidBlockIterator blockIterator = new CuboidBlockIterator(
                    (int) fixBox.minX,
                    (int) fixBox.minY,
                    (int) fixBox.minZ,
                    (int) fixBox.maxX,
                    (int) fixBox.maxY,
                    (int) fixBox.maxZ
            );
            int i = 0; // i dont just block iterators

            while(blockIterator.step()) {
                if(i >= 1000) break; // to be safe
                BlockPos pos = new BlockPos(
                        blockIterator.getX(),
                        blockIterator.getY(),
                        blockIterator.getZ()
                );

                BlockState state = this.getWorld().getBlockState(pos);
                Block block = state.getBlock();

                if(block instanceof AbstractCrackedBlock cracked) {
                    this.getWorld().setBlockState(pos, cracked.getFixedBlock());
                }

                i++;
            }
            this.discard();
            ci.cancel();
        }
    }
}
