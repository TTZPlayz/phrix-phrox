package com.ttzplayz.phrixphrox.datagen;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.blocks.PPBlocks;
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

        itemModels.generateFlatItem(PPItems.PEWTER_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(PPItems.PEWTER_AXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        // Blocks

        blockModels.createRotatedPillarWithHorizontalVariant(PPBlocks.PIPE_REMNANT.get(), TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);
        blockModels.createTrivialCube(PPBlocks.LIMESTONE.get()); //commented out later?
        blockModels.createTrivialCube(PPBlocks.POLISHED_LIMESTONE.get()); //commented out later?
    }
}
