package com.ttzplayz.phrixphrox.curse.sun_burning;

import com.ttzplayz.phrixphrox.curse.PPEffects;
import com.ttzplayz.phrixphrox.data.PPAttachments;
import com.ttzplayz.phrixphrox.particle.PPParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber
public class SunBurningCurse {

    private static final int BURN_TICKS = 8 * 20;
    private static final int REKINDLE_BELOW = 20;

    private static final int WARNING_INTERVAL = 30 * 20;
    private static final int WARNING_COUNT = 3;

    private static final int DOUSE_NOTICE_INTERVAL = 5 * 20;

    private static final int PARTICLE_COUNT = 2;
    private static final double PARTICLE_SPREAD_XZ = 0.35;
    private static final double PARTICLE_SPREAD_Y = 0.7;
    private static final double PARTICLE_SPEED = 0.02;

    private static final Map<UUID, Long> lastDouseNotice = new HashMap<>();

    public static boolean isCharred(Entity entity) {
        return entity instanceof LivingEntity living
                && living.hasEffect(PPEffects.SUN_BURNING)
                && PPEffects.isEscalated(living, PPEffects.SUN_BURNING);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!player.hasEffect(PPEffects.SUN_BURNING)) {
            PPAttachments.setCursedFlames(player, false);
            return;
        }

        boolean charred = isCharred(player);

        if (player.isAlive() && inSunlight(player)) {
            if (charred) {
                scorch(player);
            } else {
                warn(player);
            }
        }

        PPAttachments.setCursedFlames(player, charred && player.isOnFire());
    }

    private static void scorch(ServerPlayer player) {
        if (player.getRemainingFireTicks() < REKINDLE_BELOW) {
            player.igniteForTicks(BURN_TICKS);
        }

        ServerLevel level = player.level();
        level.sendParticles(PPParticles.CURSE_FLAME.get(),
                player.getX(), player.getY() + player.getBbHeight() * 0.5, player.getZ(),
                PARTICLE_COUNT, PARTICLE_SPREAD_XZ, PARTICLE_SPREAD_Y, PARTICLE_SPREAD_XZ, PARTICLE_SPEED);
    }

    public static boolean resistsDousing(Entity entity) {
        return entity instanceof ServerPlayer player
                && player.isAlive()
                && isCharred(player)
                && inSunlight(player);
    }

    public static void noticeDousing(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) return;

        long gameTime = player.level().getGameTime();
        Long last = lastDouseNotice.get(player.getUUID());
        if (last != null && gameTime - last < DOUSE_NOTICE_INTERVAL && gameTime >= last) return;
        lastDouseNotice.put(player.getUUID(), gameTime);

        player.sendSystemMessage(Component.translatable("gui.phrixphrox.curse.sun_burning.unquenchable")
                .withStyle(ChatFormatting.GOLD), true);
    }

    public static boolean inSunlight(ServerPlayer player) {
        Level level = player.level();
        if (!level.isBrightOutside()) return false;

        BlockPos eye = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        if (level.canSeeSky(eye)) return !level.isRainingAt(eye);

        BlockPos surface = surfaceAbove(level, eye);
        return surface != null && !level.isRainingAt(surface);
    }

    private static BlockPos surfaceAbove(Level level, BlockPos pos) {
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
        if (pos.getY() >= surfaceY) return null;

        BlockPos.MutableBlockPos cursor = pos.mutable();
        for (int y = pos.getY(); y < surfaceY; y++) {
            cursor.setY(y);
            BlockState state = level.getBlockState(cursor);
            if (state.getLightDampening() > 0 && state.getFluidState().isEmpty()) return null;
        }

        BlockPos surface = new BlockPos(pos.getX(), surfaceY, pos.getZ());
        return level.canSeeSky(surface) ? surface : null;
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        lastDouseNotice.remove(event.getEntity().getUUID());
    }

    private static void warn(ServerPlayer player) {
        if (player.tickCount % WARNING_INTERVAL != 0) return;

        int line = player.getRandom().nextInt(WARNING_COUNT) + 1;
        player.sendSystemMessage(Component.translatable("gui.phrixphrox.curse.sun_burning.warning." + line)
                .withStyle(ChatFormatting.GOLD), true);
    }
}
