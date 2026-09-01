package com.ttzplayz.phrixphrox.block;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.items.PPItems;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;
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

    public static final DeferredBlock<Block> LIMESTONE_STAIRS = registerBlock("limestone_stairs",
            properties -> new StairBlock(PPBlocks.LIMESTONE.get().defaultBlockState(),
                    properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> LIMESTONE_SLAB = registerBlock("limestone_slab",
            properties -> new SlabBlock(properties.strength(3f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final DeferredBlock<Block> LIMESTONE_WALL = registerBlock("limestone_wall",
            properties -> new WallBlock(properties.strength(2F)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final DeferredBlock<Block> POLISHED_LIMESTONE_STAIRS = registerBlock("polished_limestone_stairs",
            properties -> new StairBlock(PPBlocks.POLISHED_LIMESTONE.get().defaultBlockState(),
                    properties.strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> POLISHED_LIMESTONE_SLAB = registerBlock("polished_limestone_slab",
            properties -> new SlabBlock(properties.strength(3f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final DeferredBlock<Block> POLISHED_LIMESTONE_BUTTON = registerBlock("polished_limestone_button",
            properties -> new ButtonBlock(BlockSetType.STONE, 20, properties
                    .noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> POLISHED_LIMESTONE_WALL = registerBlock("polished_limestone_wall",
            properties -> new WallBlock(properties.strength(2F)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final DeferredBlock<Block> WRITING_DESK = registerBlock("writing_desk",
            properties -> new WritingDeskBlock(properties.strength(3F).requiresCorrectToolForDrops()));

    public static final List<DeferredBlock<Block>> ALL_BLOCKS = Arrays.asList(
            PIPE_REMNANT,
            LIMESTONE,
            POLISHED_LIMESTONE,
            WRITING_DESK,

            LIMESTONE_STAIRS,
            LIMESTONE_SLAB,
            LIMESTONE_WALL,

            POLISHED_LIMESTONE_STAIRS,
            POLISHED_LIMESTONE_SLAB,
            POLISHED_LIMESTONE_BUTTON,
            POLISHED_LIMESTONE_WALL
    );

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
