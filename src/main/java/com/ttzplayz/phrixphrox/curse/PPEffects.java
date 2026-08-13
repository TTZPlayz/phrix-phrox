package com.ttzplayz.phrixphrox.curse;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.particle.PPParticles;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PPEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, PhrixPhrox.MOD_ID);

//    public static final Holder<MobEffect> GENERIC_CURSE = MOB_EFFECTS.register("generic_curse",
//            () -> new CurseEffect(ParticleTypes.COPPER_FIRE_FLAME));

    public static final Holder<MobEffect> HOLLOW_VOICE = MOB_EFFECTS.register("hollow_voice",
            () -> new CurseEffect(PPParticles.CURSE_PARTICLE.get()));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }

}
