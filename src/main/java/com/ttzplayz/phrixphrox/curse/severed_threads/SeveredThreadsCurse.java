package com.ttzplayz.phrixphrox.curse.severed_threads;

import com.ttzplayz.phrixphrox.curse.PPEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
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

import static net.minecraft.sounds.SoundEvents.VILLAGER_NO;

@EventBusSubscriber
public class SeveredThreadsCurse {

    @SubscribeEvent
    public static void onCursed(MobEffectEvent.Added event) {
        if (event.getEntity() instanceof ServerPlayer player && player.hasEffect(PPEffects.SEVERED_THREADS)) {
            sever(event, event.getEntity());
            deglorify(event, event.getEntity());
        }
    }

    @SubscribeEvent
    public static void maintain(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() instanceof Player player && event.getItemStack().getItem() == Items.LEAD) {
            if (sever(event, player)) {
                event.setCanceled(true);
            }
        }
    }

    private static void deglorify(@Nullable Event event, Entity entity) {
        if (entity instanceof ServerPlayer player && player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            player.removeEffect(MobEffects.HERO_OF_THE_VILLAGE);
            player.sendSystemMessage(Component.literal("Your heroism seems to be futile..."), true);
        }
    }

    private static boolean sever(@Nullable Event event, Entity entity) {
        if (entity instanceof ServerPlayer player && player.hasEffect(PPEffects.SEVERED_THREADS)) {
            player.shearOffAllLeashConnections(player);
            player.sendSystemMessage(Component.literal("Your leash has been severed..."), true);
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onVillagerInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() instanceof ServerPlayer player && player.hasEffect(PPEffects.SEVERED_THREADS)) {
            if (event.getTarget() instanceof Villager villager) {
                distrust(event, villager, player);
            }
        }
    }

    public static void distrust(@Nullable Event event, Villager villager, ServerPlayer player) {
        villager.getGossips().remove(player.getUUID(), GossipType.MINOR_POSITIVE);
        villager.getGossips().remove(player.getUUID(), GossipType.MAJOR_POSITIVE);
        int prevReputation = villager.getGossips().getReputation(player.getUUID(),
                type -> type == GossipType.MINOR_NEGATIVE);
        if (prevReputation == 0) {
            villager.getGossips().remove(player.getUUID(), GossipType.MINOR_NEGATIVE);
            villager.getGossips().add(player.getUUID(), GossipType.MINOR_NEGATIVE, 25);
        }
        villager.makeSound(VILLAGER_NO);
//        player.sendSystemMessage(Component.literal("previous reputation:" + prevReputation), false);

//        player.sendSystemMessage(Component.literal("Current reputation:" + villager.getGossips().getReputation(player.getUUID(),
//                type -> type == GossipType.MINOR_NEGATIVE)), false);
    }
}