package com.ttzplayz.phrixphrox.curse.hollow_voice;

import com.ttzplayz.phrixphrox.curse.PPEffects;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

@EventBusSubscriber
public class HollowVoiceCurse {

    public static boolean isMute(Entity entity) {
        return entity instanceof LivingEntity living
                && living.hasEffect(PPEffects.HOLLOW_VOICE)
                && PPEffects.isEscalated(living, PPEffects.HOLLOW_VOICE);
    }

    public static void notifySilenced(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("You seem to be silenced..."), true);
    }

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        silence(event, event.getPlayer(), event.getRawText());
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !isMute(player)) return;

        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
        if (!(blockEntity instanceof SignBlockEntity)) return;

        event.setCanceled(true);
        notifySilenced(player);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !isMute(player)) return;
        if (!event.getItemStack().is(Items.WRITABLE_BOOK)) return;

        event.setCanceled(true);
        notifySilenced(player);
    }

    private static void silence(@Nullable Event event, Entity entity, String text) {
        if (entity == null || text == null)
            return;
        if (entity instanceof LivingEntity living && living.hasEffect(PPEffects.HOLLOW_VOICE)) {
            if (!text.startsWith("/") || text.startsWith("/msg ")) {
                if (event instanceof ICancellableEvent cancellable) {
                    cancellable.setCanceled(true);
                }
                if (entity instanceof ServerPlayer player) {
                    player.sendSystemMessage(Component.literal((("<" + entity.getDisplayName().getString() + "> ") + "§k" + text)), false);
                    notifySilenced(player);
                    player.shearOffAllLeashConnections(player);
                }
            }
        }
    }
}
