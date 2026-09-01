package com.ttzplayz.phrixphrox.datagen;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.block.PPBlocks;
import com.ttzplayz.phrixphrox.items.*;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class PPModelProvider extends ModelProvider {

    public PPModelProvider(PackOutput output) {
        super(output, PhrixPhrox.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // Items

        for (DeferredItem<Item> item : PPItems.FLAT_ITEMS) {
            itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
        }
        for (DeferredItem<Item> item : PPItems.TOOL_ITEMS) {
            itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        }

        // Blocks

        blockModels.createRotatedPillarWithHorizontalVariant(PPBlocks.PIPE_REMNANT.get(), TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);
//        blockModels.createTrivialCube(PPBlocks.LIMESTONE.get()); //commented out NOW?
//        blockModels.createTrivialCube(PPBlocks.POLISHED_LIMESTONE.get()); //commented out now.
        blockModels.createNonTemplateHorizontalBlock(PPBlocks.WRITING_DESK.get());

        blockModels.family(PPBlocks.LIMESTONE.get())
                .stairs(PPBlocks.LIMESTONE_STAIRS.get())
                .slab(PPBlocks.LIMESTONE_SLAB.get())
                .wall(PPBlocks.LIMESTONE_WALL.get());
        blockModels.family(PPBlocks.POLISHED_LIMESTONE.get())
                .stairs(PPBlocks.POLISHED_LIMESTONE_STAIRS.get())
                .slab(PPBlocks.POLISHED_LIMESTONE_SLAB.get())
                .button(PPBlocks.POLISHED_LIMESTONE_BUTTON.get())
                .wall(PPBlocks.POLISHED_LIMESTONE_WALL.get());
    }
}
