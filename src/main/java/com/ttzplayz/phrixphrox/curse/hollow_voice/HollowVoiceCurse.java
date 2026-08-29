package com.ttzplayz.phrixphrox.curse.hollow_voice;

import com.ttzplayz.phrixphrox.curse.PPEffects;
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
public class HollowVoiceCurse {
    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        silence(event, event.getPlayer(), event.getRawText());
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
                    player.sendSystemMessage(Component.literal("You seem to be silenced..."), true);
                    player.shearOffAllLeashConnections(player);
                }
            }
        }
    }
}