package com.ttzplayz.phrixphrox.curse.blunder_strike;

import java.util.Random;
import java.util.random.RandomGenerator;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.curse.PPEffects;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

@EventBusSubscriber
class BlunderStrikeCurse {
    public static final ResourceKey<DamageType> BLUNDER_STRIKE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "blunder_strike"));

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.hasEffect(PPEffects.SEVERED_THREADS)) {
            // TODO: implement increasing severity
            if (RandomGenerator.getDefault().nextInt(10) > 7) {
                DamageSource damageSource = new DamageSource(event.getTarget().level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(BLUNDER_STRIKE_DAMAGE), null, null, null);

                player.hurtClient(damageSource);
                player.sendSystemMessage(Component.literal("You seem to have cut yourself").withColor(TextColor.RED));
            }

        }
    }


}
