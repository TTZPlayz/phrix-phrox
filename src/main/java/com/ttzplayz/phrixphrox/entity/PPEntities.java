package com.ttzplayz.phrixphrox.entity;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = PhrixPhrox.MOD_ID)
public class PPEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, PhrixPhrox.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<LeadenServant>> LEADEN_SERVANT =
            ENTITY_TYPES.register("leaden_servant", id -> EntityType.Builder
                    .of(LeadenServant::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.0F)
                    .eyeHeight(0.5F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    @SubscribeEvent
    public static void onCreateAttributes(EntityAttributeCreationEvent event) {
        event.put(LEADEN_SERVANT.get(), LeadenServant.createAttributes().build());
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
