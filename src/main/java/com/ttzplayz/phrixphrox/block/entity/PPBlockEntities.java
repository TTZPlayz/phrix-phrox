package com.ttzplayz.phrixphrox.block.entity;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.block.PPBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PPBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, PhrixPhrox.MOD_ID);

    public static final Supplier<BlockEntityType<WritingDeskBlockEntity>> WRITING_DESK_BE =
            BLOCK_ENTITIES.register("pedestal_be", () -> new BlockEntityType<>(
                    WritingDeskBlockEntity::new, PPBlocks.WRITING_DESK.get()));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
