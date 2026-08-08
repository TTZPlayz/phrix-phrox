package com.ttzplayz.phrixphrox.blocks;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.items.PPItems;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PPBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PhrixPhrox.MOD_ID);

    public static final DeferredBlock<Block> PIPE_REMNANT = registerBlock("pipe_remnant", properties ->
        new PipeRemnant(properties.strength(3f))
    );
    public static final DeferredBlock<Block> LIMESTONE = registerBlock("limestone", properties ->
            new PipeRemnant(properties.strength(4f).requiresCorrectToolForDrops())
    );
    public static final DeferredBlock<Block> POLISHED_LIMESTONE = registerBlock("polished_limestone", properties ->
            new PipeRemnant(properties.strength(4f).requiresCorrectToolForDrops())
    );

    public static final List<DeferredBlock<Block>> ALL_BLOCKS = Arrays.asList(PIPE_REMNANT, LIMESTONE, POLISHED_LIMESTONE);

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        PPItems.ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
