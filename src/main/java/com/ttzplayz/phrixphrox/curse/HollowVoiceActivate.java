package com.ttzplayz.phrixphrox.curse;

import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

@EventBusSubscriber
public class HollowVoiceActivate {
    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        execute(event, event.getPlayer(), event.getRawText());
    }

    private static void execute(@Nullable Event event, Entity entity, String text) {
        if (entity == null || text == null)
            return;
        if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(PPEffects.HOLLOW_VOICE)) {
            if (!text.startsWith("/")) {
                if (event instanceof ICancellableEvent _cancellable) {
                    _cancellable.setCanceled(true);
                }
                if (entity instanceof ServerPlayer _player)
                    _player.sendSystemMessage(Component.literal((("<" + entity.getDisplayName().getString() + "> ") + "§k" + text)), false);
                if (entity instanceof ServerPlayer _player)
                    _player.sendSystemMessage(Component.literal("You seem to be silenced..."), true);
            }
        }
    }
}