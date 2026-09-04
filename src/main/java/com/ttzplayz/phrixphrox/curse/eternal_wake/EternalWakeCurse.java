package com.ttzplayz.phrixphrox.curse.eternal_wake;

import com.ttzplayz.phrixphrox.Config;
import com.ttzplayz.phrixphrox.curse.PPEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSpawnPhantomsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber
public class EternalWakeCurse {

    private static final int RESTLESS_TICKS = 15 * 20;

    private static final int WAKE_DELAY_MIN = 40;
    private static final int WAKE_DELAY_MAX = 160;

    private static final long NO_WAKE = 0L;

    private record Vigil(long wakeAt, long restlessUntil) {
        static final Vigil NONE = new Vigil(NO_WAKE, 0L);

        Vigil waking(long at) {
            return new Vigil(at, restlessUntil);
        }

        Vigil restingUntil(long until) {
            return new Vigil(NO_WAKE, until);
        }
    }

    private static final Map<UUID, Vigil> vigils = new HashMap<>();

    public static boolean isSleepless(Entity entity) {
        return entity instanceof LivingEntity living
                && living.hasEffect(PPEffects.ETERNAL_WAKE)
                && PPEffects.isEscalated(living, PPEffects.ETERNAL_WAKE);
    }

    @SubscribeEvent
    public static void onSleepAttempt(CanPlayerSleepEvent event) {
        ServerPlayer player = event.getEntity();
        if (!player.hasEffect(PPEffects.ETERNAL_WAKE)) return;
        if (event.getProblem() != null) return;

        if (isSleepless(player)) {
            event.setProblem(problem("no_sleep"));
            return;
        }

        long gameTime = player.level().getGameTime();
        Vigil vigil = vigils.getOrDefault(player.getUUID(), Vigil.NONE);
        if (gameTime < vigil.restlessUntil()) {
            event.setProblem(problem("cant_sleep"));
            return;
        }

        RandomSource random = player.getRandom();
        if (random.nextDouble() >= Config.ETERNAL_WAKE_WAKE_CHANCE.getAsDouble()) {
            vigils.put(player.getUUID(), vigil.waking(NO_WAKE));
            return;
        }

        int delay = WAKE_DELAY_MIN + random.nextInt(WAKE_DELAY_MAX - WAKE_DELAY_MIN + 1);
        vigils.put(player.getUUID(), vigil.waking(gameTime + delay));
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.hasEffect(PPEffects.ETERNAL_WAKE)) {
            vigils.remove(player.getUUID());
            return;
        }

        breakSleep(player);

        if (!isSleepless(player) || !player.isAlive()) return;

        player.causeFoodExhaustion((float) Config.ETERNAL_WAKE_EXHAUSTION.getAsDouble());
    }

    @SubscribeEvent
    public static void onSpawnPhantoms(PlayerSpawnPhantomsEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!isSleepless(player)) return;
        if (!event.shouldSpawnPhantoms(player.level(), player.blockPosition())) return;

        event.setResult(PlayerSpawnPhantomsEvent.Result.ALLOW);
    }

    private static void breakSleep(ServerPlayer player) {
        Vigil vigil = vigils.get(player.getUUID());
        if (vigil == null || vigil.wakeAt() == NO_WAKE) return;

        long gameTime = player.level().getGameTime();
        if (gameTime < vigil.wakeAt()) return;

        if (!player.isSleeping()) {
            vigils.put(player.getUUID(), vigil.waking(NO_WAKE));
            return;
        }

        player.stopSleepInBed(true, true);
        vigils.put(player.getUUID(), vigil.restingUntil(gameTime + RESTLESS_TICKS));
        player.sendSystemMessage(Component.translatable("gui.phrixphrox.curse.eternal_wake.startled")
                .withStyle(ChatFormatting.DARK_AQUA), true);
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        vigils.remove(event.getEntity().getUUID());
    }

    private static Player.BedSleepingProblem problem(String reason) {
        return new Player.BedSleepingProblem(
                Component.translatable("gui.phrixphrox.curse.eternal_wake." + reason)
                        .withStyle(ChatFormatting.DARK_AQUA));
    }
}
