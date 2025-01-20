//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kingdre.gateways.client.particle;

import com.ibm.icu.message2.Mf2DataModel;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.light.AreaLight;
import foundry.veil.api.client.render.light.Light;
import foundry.veil.api.client.render.light.PointLight;
import foundry.veil.impl.client.render.pipeline.VeilBloomRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class FlashParticle extends AnimatedParticle {

    PointLight light;
    float brightness, smoothness;


    FlashParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 0);

        this.brightness = 50;

        this.light = new PointLight()
                .setPosition(x, y, z)
                .setRadius(20)
                .setBrightness(this.brightness);

        // brightness decay equation: https://www.desmos.com/calculator/gy5out7fur

        this.setMaxAge(160);
        this.smoothness = 1f;
        this.setSpriteForAge(spriteProvider);
        VeilRenderSystem.renderer().getLightRenderer().addLight(light);
    }

    @Override
    public void tick() {
        super.tick();
//        VeilRenderSystem.renderer().getLightRenderer().

        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            double a = this.brightness + this.smoothness;
            double c = Math.log10(this.smoothness / a) / -this.maxAge;

            this.brightness = (float) (a * (Math.pow(10, -c*this.age)) - this.smoothness);
            this.light.setBrightness(this.brightness);
        }
    }

    @Override
    public void markDead() {
        super.markDead();
        VeilRenderSystem.renderer().getLightRenderer().removeLight(light);
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new FlashParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}
