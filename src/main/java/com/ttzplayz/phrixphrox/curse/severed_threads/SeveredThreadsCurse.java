package com.ttzplayz.phrixphrox.curse.severed_threads;

import com.ttzplayz.phrixphrox.curse.PPEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import javax.annotation.Nullable;

import static net.minecraft.sounds.SoundEvents.VILLAGER_NO;

@EventBusSubscriber
public class SeveredThreadsCurse {

    private static final float FLEE_RADIUS = 10.0f;
    private static final double FLEE_WALK_SPEED = 0.6;
    private static final double FLEE_SPRINT_SPEED = 0.8;
    private static final int UNHAPPY_TICKS = 40;
    private static final int GOLEM_TARGET_INTERVAL = 10;

    public static boolean isShunned(Entity entity) {
        return entity instanceof net.minecraft.world.entity.LivingEntity living
                && living.hasEffect(PPEffects.SEVERED_THREADS)
                && PPEffects.isEscalated(living, PPEffects.SEVERED_THREADS);
    }

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

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;

        if (event.getEntity() instanceof Villager villager) {
            villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, Player.class,
                    FLEE_RADIUS, FLEE_WALK_SPEED, FLEE_SPRINT_SPEED, SeveredThreadsCurse::isShunned));
        } else if (event.getEntity() instanceof IronGolem golem) {
            golem.targetSelector.addGoal(GOLEM_TARGET_INTERVAL, new NearestAttackableTargetGoal<>(
                    golem, Player.class, GOLEM_TARGET_INTERVAL, true, false,
                    (target, level) -> isShunned(target)));
        }
    }

    @SubscribeEvent
    public static void onTradeClosed(PlayerContainerEvent.Close event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(event.getContainer() instanceof MerchantMenu)) return;
        if (!player.hasEffect(PPEffects.SEVERED_THREADS) || isShunned(player)) return;

        player.sendSystemMessage(Component.literal("This villager seems to distrust you\u2026"), true);
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

                if (isShunned(player)) {
                    villager.setUnhappyCounter(UNHAPPY_TICKS);
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void releasePets(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;

        for (TamableAnimal pet : level.getEntitiesOfClass(TamableAnimal.class, player.getBoundingBox().inflate(256.0))) {
            if (pet.getOwnerReference() == null) continue;
            if (!player.getUUID().equals(pet.getOwnerReference().getUUID())) continue;

            pet.setOwnerReference(null);
            pet.setTame(false, true);
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
