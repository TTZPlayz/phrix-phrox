package com.ttzplayz.phrixphrox.curse;

import net.minecraft.core.particles.ParticleOptions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import org.jspecify.annotations.NonNull;

@EventBusSubscriber
public class CurseEffect extends MobEffect {
    public CurseEffect(ParticleOptions particleOptions) {
        super(MobEffectCategory.NEUTRAL, 0xb4f478, particleOptions);
    }


    @SubscribeEvent
    public static void registerMobEffectExtensions(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(@NonNull MobEffectInstance effect) {
                return false;
            }

            @Override
            public boolean isVisibleInGui(@NonNull MobEffectInstance effect) {
                return false;
            }
        }, PPEffects.HOLLOW_VOICE
            //add more later :)
                );
    }
}