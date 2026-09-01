package com.ttzplayz.phrixphrox.particle;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PPParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, PhrixPhrox.MOD_ID);

    public static final Supplier<SimpleParticleType> CURSE_PARTICLE =
            PARTICLE_TYPES.register("curse", () -> new SimpleParticleType(true));


    public static final Supplier<SimpleParticleType> CURSE_FLAME =
            PARTICLE_TYPES.register("curse_flame", () -> new SimpleParticleType(true));


    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}