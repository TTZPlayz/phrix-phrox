package com.ttzplayz.phrixphrox.curse;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.particle.PPParticles;
import com.ttzplayz.phrixphrox.saveddata.CurseInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
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
    public static final Holder<MobEffect> BLUNDER_STRIKE = MOB_EFFECTS.register("blunder_strike",
            () -> new CurseEffect(PPParticles.CURSE_PARTICLE.get()));
    public static final Holder<MobEffect> STARVED_FARMER = MOB_EFFECTS.register("starved_farmer",
            () -> new CurseEffect(PPParticles.CURSE_PARTICLE.get()));
    public static final Holder<MobEffect> LOST_TRAVELER = MOB_EFFECTS.register("lost_traveler",
            () -> new CurseEffect(PPParticles.CURSE_PARTICLE.get()));
    public static final Holder<MobEffect> TIMELOCKED_CLOCKMAKER = MOB_EFFECTS.register("timelocked_clockmaker",
            () -> new CurseEffect(PPParticles.CURSE_PARTICLE.get()));

    public static final List<Holder<MobEffect>> ALL_CURSES = Arrays.asList(
            HOLLOW_VOICE,
            SEVERED_THREADS,
            BLUNDER_STRIKE
    );


    public static Holder<MobEffect> effectFor(CurseInstance.Curse curse) {
        if (curse == null) return null;
        return switch (curse) {
            case HollowVoice -> HOLLOW_VOICE;
            case SeveredThreads -> SEVERED_THREADS;
            case BlunderStrike -> BLUNDER_STRIKE;
            default -> null;
        };
    }

    public static boolean isEscalated(LivingEntity entity, Holder<MobEffect> effect) {
        MobEffectInstance instance = entity.getEffect(effect);
        return instance != null && instance.getAmplifier() >= STAGE_ESCALATED;
    }

    public static final int STAGE_INITIAL = 0;
    public static final int STAGE_ESCALATED = 1;

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }

}
