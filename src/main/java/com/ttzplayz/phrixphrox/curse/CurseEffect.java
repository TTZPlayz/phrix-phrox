package com.ttzplayz.phrixphrox.curse;

import com.ttzplayz.phrixphrox.items.PPItems;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jspecify.annotations.NonNull;

@EventBusSubscriber
public class CurseEffect extends MobEffect {
    public CurseEffect(ParticleOptions particleOptions) {
        super(MobEffectCategory.NEUTRAL, 0xb4f478, particleOptions);
    }


    @SubscribeEvent
    public static void registerMobEffectExtensions(RegisterClientExtensionsEvent event) {
        event.registerMobEffect((IClientMobEffectExtensions) new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(@NonNull MobEffectInstance effect) {
                return false;
            }

            @Override
            public boolean isVisibleInGui(@NonNull MobEffectInstance effect) {
                return false;
            }
        },
                PPEffects.ALL_CURSES.toArray(Holder[]::new)
                //add more later :)
                );
    }
}