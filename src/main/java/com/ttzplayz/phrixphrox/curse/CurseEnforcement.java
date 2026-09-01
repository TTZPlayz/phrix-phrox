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
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.UUID;

@EventBusSubscriber
public class CurseEnforcement {

    private static final int SCAN_INTERVAL = 20;

    public static final long ESCALATION_TICKS = 17L * 60L * 20L;

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

            if (!curse.isEscalated() && gameTime - curse.activatedAt() >= ESCALATION_TICKS) {
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

    private static void lift(PlayerCurseData data, Long curseId, CurseInstance curse, ServerLevel level) {
        if (!data.neutralize(curseId)) return;

        Holder<MobEffect> effect = PPEffects.effectFor(curse.curse());
        for (CurseInstance.CurseTarget target : curse.targets()) {
            if (!(level.getPlayerByUUID(target.id()) instanceof ServerPlayer victim)) continue;
            if (effect != null) victim.removeEffect(effect);
            victim.sendSystemMessage(Component.translatable("gui.phrixphrox.curse.neutralized")
                    .withStyle(ChatFormatting.GREEN), true);
        }
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
