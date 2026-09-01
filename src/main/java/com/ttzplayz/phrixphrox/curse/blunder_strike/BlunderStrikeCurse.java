package com.ttzplayz.phrixphrox.curse.blunder_strike;

import java.util.random.RandomGenerator;

import com.ttzplayz.phrixphrox.Config;
import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.curse.PPEffects;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

@EventBusSubscriber
public class BlunderStrikeCurse {
    public static final ResourceKey<DamageType> BLUNDER_STRIKE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "blunder_strike"));

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.hasEffect(PPEffects.BLUNDER_STRIKE)) return;
        if (!(player.level() instanceof ServerLevel level)) return;

        double chance = PPEffects.isEscalated(player, PPEffects.BLUNDER_STRIKE)
                ? Config.BLUNDER_STRIKE_CHANCE_ESCALATED.getAsDouble()
                : Config.BLUNDER_STRIKE_CHANCE.getAsDouble();
        if (RandomGenerator.getDefault().nextDouble() >= chance) return;

        event.setCanceled(true);

        DamageSource damageSource = new DamageSource(
                level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(BLUNDER_STRIKE_DAMAGE));
        player.hurtServer(level, damageSource, (float) Config.BLUNDER_STRIKE_DAMAGE.getAsDouble());
        player.sendSystemMessage(Component.translatable("gui.phrixphrox.curse.blunder_strike.miss")
                .withStyle(ChatFormatting.RED), true);
    }
}
