package com.ttzplayz.phrixphrox.curse.severed_threads;

import com.ttzplayz.phrixphrox.curse.PPEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;

@EventBusSubscriber
public class SeveredThreadsCurse {

    @SubscribeEvent
    public static void onCursed(MobEffectEvent.Added event) {
        sever(event, event.getEntity());
    }
    // 大丈夫ですか？

    @SubscribeEvent
    public static void maintain(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() instanceof Player player && event.getItemStack().getItem() == Items.LEAD) {
            if (sever(event, player)) {
                event.setCanceled(true);
            }
        }
    }
    // ちょっと待って...

    private static boolean sever(@Nullable Event event, Entity entity) {
        if (entity instanceof ServerPlayer player && player.hasEffect(PPEffects.SEVERED_THREADS)) {
            player.shearOffAllLeashConnections(player);
            player.sendSystemMessage(Component.literal("Your leash has been severed..."), true);
            return true;
        }
        return false;
    }
// 大丈夫!

}