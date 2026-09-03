package com.ttzplayz.phrixphrox.curse;

import com.ttzplayz.phrixphrox.data.PPData;
import com.ttzplayz.phrixphrox.saveddata.CurseInstance;
import com.ttzplayz.phrixphrox.saveddata.PlayerCurseData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EventBusSubscriber
public class CurseEnforcement {

    private static final int SCAN_INTERVAL = 20;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.tickCount % SCAN_INTERVAL != 0) return;

        ServerLevel level = player.level();
        PlayerCurseData data = PlayerCurseData.get(level);
        long gameTime = level.getGameTime();

        data.forEachCurseBy(player.getUUID(), (curseId, curse) -> {
            if (carriesCursedItem(player, curseId)) {
                lift(data, curseId, curse, level);
            }
        });

        data.forEachAffliction(player.getUUID(), (curseId, curse) -> {
            Holder<MobEffect> effect = PPEffects.effectFor(curse.curse());
            if (effect == null) return;

            CurseInstance.CurseTarget curser = curse.curser();
            if (curser != null && carriesHeadOf(player, curser.id())) {
                lift(data, curseId, curse, level);
                return;
            }

            CurseInstance.Curse kind = curse.curse();
            if (!curse.isEscalated() && kind != null
                    && gameTime - curse.activatedAt() >= kind.escalationTicks()) {
                escalate(data, curseId, curse, player);
            }

            int stage = curse.isEscalated() ? PPEffects.STAGE_ESCALATED : PPEffects.STAGE_INITIAL;
            MobEffectInstance current = player.getEffect(effect);
            if (current == null || current.getAmplifier() != stage) {
                player.addEffect(new MobEffectInstance(effect, MobEffectInstance.INFINITE_DURATION,
                        stage, false, false, false));
            }
        });
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ServerLevel level = player.level();
        PlayerCurseData data = PlayerCurseData.get(level);

        data.forEachAffliction(player.getUUID(), (curseId, curse) -> {
            CurseInstance.CurseTarget curser = curse.curser();
            if (curser != null && curser.id().equals(player.getUUID())) {
                lift(data, curseId, curse, level);
            }
        });
    }

    private static void escalate(PlayerCurseData data, Long curseId, CurseInstance curse, ServerPlayer player) {
        if (!data.escalate(curseId)) return;

        CurseInstance.Curse kind = curse.curse();
        if (kind == null) return;

        player.level().playSound(null, player.blockPosition(), SoundEvents.WITHER_SPAWN,
                SoundSource.HOSTILE, 1.0F, 1.0F);
        player.sendSystemMessage(Component.translatable("gui.phrixphrox.curse.escalated." + kind.path())
                .withStyle(ChatFormatting.GREEN), true);

        if (kind == CurseInstance.Curse.SeveredThreads) {
            com.ttzplayz.phrixphrox.curse.severed_threads.SeveredThreadsCurse.releasePets(player);
        }
    }

    public static long inflict(ServerLevel level, ServerPlayer target, CurseInstance.Curse kind,
                               @Nullable ServerPlayer curser) {
        PlayerCurseData data = PlayerCurseData.get(level);

        long curseId;
        do {
            curseId = level.getRandom().nextLong();
        } while (data.curse(curseId) != null);

        data.newCurse(curseId, kind.ordinal());
        if (curser != null) {
            data.setCurser(curseId, curser.getUUID(), curser.getGameProfile().name());
        }
        data.addPlayerToCurse(curseId, target.getUUID(), target.getGameProfile().name());
        data.activate(curseId, level.getGameTime());
        return curseId;
    }

    public static int liftCurses(ServerLevel level, ServerPlayer target, CurseInstance.Curse kind) {
        PlayerCurseData data = PlayerCurseData.get(level);

        List<Long> matches = new ArrayList<>();
        data.forEachAffliction(target.getUUID(), (curseId, curse) -> {
            if (curse.curse() == kind) matches.add(curseId);
        });

        int lifted = 0;
        for (Long curseId : matches) {
            CurseInstance curse = data.curse(curseId);
            if (curse != null && lift(data, curseId, curse, level)) lifted++;
        }
        return lifted;
    }

    private static boolean lift(PlayerCurseData data, Long curseId, CurseInstance curse, ServerLevel level) {
        if (!data.neutralize(curseId)) return false;

        Holder<MobEffect> effect = PPEffects.effectFor(curse.curse());
        for (CurseInstance.CurseTarget target : curse.targets()) {
            ServerPlayer victim = level.getServer().getPlayerList().getPlayer(target.id());
            if (victim == null) continue;
            if (effect != null) victim.removeEffect(effect);
            victim.sendSystemMessage(Component.translatable("gui.phrixphrox.curse.neutralized")
                    .withStyle(ChatFormatting.GREEN), true);
        }
        return true;
    }

    private static boolean carriesCursedItem(ServerPlayer player, Long curseId) {
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (curseId.equals(stack.get(PPData.CURSED_ITEM_ID))) return true;
        }
        return false;
    }

    private static boolean carriesHeadOf(ServerPlayer player, UUID curserId) {
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (!stack.is(Items.PLAYER_HEAD)) continue;
            ResolvableProfile profile = stack.get(DataComponents.PROFILE);
            if (profile == null) continue;
            if (curserId.equals(profile.partialProfile().id())) return true;
        }
        return false;
    }
}
