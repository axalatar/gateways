package com.kingdre.gateways.mixin;

import com.kingdre.gateways.Gateways;
import com.kingdre.gateways.item.GatewaysItems;
import com.llamalad7.mixinextras.sugar.Local;
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
import net.minecraft.util.hit.HitResult;
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
public class ExperienceBottleMixin extends Entity {


    public ExperienceBottleMixin(EntityType<?> type, World world) {
        super(type, world);
    }

//    @Inject(method = "getDefaultItem", at = @At(value = "HEAD"), cancellable = true)
//    private void injected(CallbackInfoReturnable<Item> cir) {
//        cir.setReturnValue(GatewaysItems.TUNING_FORK);
//    }

    @Inject(method = "onCollision", at = @At(value = "HEAD"))
    private void injected(HitResult hitResult, CallbackInfo ci) {
                this.getWorld().getPlayers().forEach(playerEntity -> sendMessage(Text.of("abcdefg")));

        List<Entity> nearbyShards = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(3, 3, 3), (entity) -> {
            if(entity instanceof ItemEntity itemEntity) {

            this.getWorld().getPlayers().forEach(playerEntity -> sendMessage(Text.of(String.valueOf(entity))));
                Gateways.LOGGER.info(String.valueOf(entity));
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
            this.discard();
//            ci.cancel();
        }
    }


//    @Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;syncWorldEvent(ILnet/minecraft/util/math/BlockPos;I)V", shift = At.Shift.AFTER), cancellable = true)
//    @Inject(method = "onCollision", at = @At(value = "HEAD"), cancellable = true)
//    private void injected(CallbackInfo ci, @Local(argsOnly = true) HitResult hitResult) {
//        Gateways.LOGGER.info("test123");
//        this.getWorld().getPlayers().forEach(playerEntity -> sendMessage(Text.of("abcdefg")));
//
//        List<Entity> nearbyShards = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(3, 3, 3), (entity) -> {
//            if(entity instanceof ItemEntity itemEntity) {
//
//            this.getWorld().getPlayers().forEach(playerEntity -> sendMessage(Text.of(String.valueOf(entity))));
//                Gateways.LOGGER.info(String.valueOf(entity));
//                ItemStack item = itemEntity.getStack();
//                return item.getItem().equals(Items.AMETHYST_SHARD);
//
//            }
//            return false;
//        });
//
//
//        if(!nearbyShards.isEmpty()) {
//            ItemEntity item = ((ItemEntity) nearbyShards.get(0));
//            ItemStack held = item.getStack();
//
//            held.decrement(1);
//            item.setStack(held);
//            this.discard();
//            ci.cancel();
//        }
//    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
