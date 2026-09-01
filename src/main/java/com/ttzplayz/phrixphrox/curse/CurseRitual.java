package com.ttzplayz.phrixphrox.curse;

import com.ttzplayz.phrixphrox.data.PPData;
import com.ttzplayz.phrixphrox.items.PPItems;
import com.ttzplayz.phrixphrox.particle.PPParticles;
import com.ttzplayz.phrixphrox.saveddata.CurseInstance;
import com.ttzplayz.phrixphrox.saveddata.PlayerCurseData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public class CurseRitual {

    private static final int SCAN_INTERVAL = 20;
    private static final int POOL_RADIUS = 3;

    private static final int PARTICLE_COUNT = 60;
    private static final double PARTICLE_SPREAD = 0.6;
    private static final double PARTICLE_SPEED = 0.05;

    @SubscribeEvent
    public static void onItemTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ItemEntity item)) return;
        if (!(item.level() instanceof ServerLevel level)) return;
        if (item.tickCount % SCAN_INTERVAL != 0) return;

        ItemStack stack = item.getItem();
        if (!castable(stack)) return;

        Long curseId = stack.get(PPData.DEFIXION_ID);
        if (curseId == null) return;

        PlayerCurseData data = PlayerCurseData.get(level);
        CurseInstance curse = data.curse(curseId);
        if (curse == null || curse.isActive() || curse.isNeutralized()) return;

        if (!inPool(level, item.blockPosition())) return;
        if (!data.activate(curseId, level.getGameTime())) return;

        level.sendParticles(PPParticles.CURSE_FLAME.get(), item.getX(), item.getY() + 0.2, item.getZ(),
                PARTICLE_COUNT, PARTICLE_SPREAD, PARTICLE_SPREAD, PARTICLE_SPREAD, PARTICLE_SPEED);
        level.playSound(null, item.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
        item.discard();
    }

    private static boolean castable(ItemStack stack) {
        if (stack.is(PPItems.BOUND_DEFIXION)) return true;
        return stack.is(PPItems.DEFIXION) && stack.get(PPData.TARGET_ID) != null;
    }

    private static boolean inPool(ServerLevel level, BlockPos center) {
        if (!level.getFluidState(center).is(FluidTags.WATER)) return false;

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -POOL_RADIUS; dx <= POOL_RADIUS; dx++) {
            for (int dz = -POOL_RADIUS; dz <= POOL_RADIUS; dz++) {
                cursor.set(center.getX() + dx, center.getY(), center.getZ() + dz);
                if (!level.getFluidState(cursor).is(FluidTags.WATER)) return false;
            }
        }
        return true;
    }
}
