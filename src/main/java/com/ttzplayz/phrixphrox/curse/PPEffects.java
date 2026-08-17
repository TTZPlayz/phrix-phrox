package com.ttzplayz.phrixphrox.curse;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.particle.PPParticles;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.List;

public class PPEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, PhrixPhrox.MOD_ID);

//    public static final Holder<MobEffect> GENERIC_CURSE = MOB_EFFECTS.register("generic_curse",
//            () -> new CurseEffect(ParticleTypes.COPPER_FIRE_FLAME));

    public static final Holder<MobEffect> HOLLOW_VOICE = MOB_EFFECTS.register("hollow_voice",
            () -> new CurseEffect(PPParticles.CURSE_PARTICLE.get()));
    public static final Holder<MobEffect> SEVERED_THREADS = MOB_EFFECTS.register("severed_threads",
            () -> new CurseEffect(PPParticles.CURSE_PARTICLE.get()));

    public static final List<Holder<MobEffect>> ALL_CURSES = Arrays.asList(
            HOLLOW_VOICE,
            SEVERED_THREADS
    );


    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }

}
