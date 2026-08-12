package com.ttzplayz.phrixphrox.data;

import com.mojang.serialization.Codec;
import com.ttzplayz.phrixphrox.PhrixPhrox;
import java.util.function.UnaryOperator;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PPData {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(
        Registries.DATA_COMPONENT_TYPE,
        PhrixPhrox.MOD_ID
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> DEFIXION_ID = register("defixion-id", builder ->
        builder.persistent(Codec.LONG)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> CURSED_ITEM_ID = register("cursed-item-id", builder ->
        builder.persistent(Codec.LONG)
    );

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(
        String name,
        UnaryOperator<DataComponentType.Builder<T>> builderOperator
    ) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
