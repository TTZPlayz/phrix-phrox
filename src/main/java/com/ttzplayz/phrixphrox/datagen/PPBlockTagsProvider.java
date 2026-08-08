package com.ttzplayz.phrixphrox.datagen;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.blocks.PPBlocks;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

public class PPBlockTagsProvider extends BlockTagsProvider {

    public PPBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, PhrixPhrox.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(PPBlocks.PIPE_REMNANT.getKey())
                .add(PPBlocks.LIMESTONE.getKey())
                .add(PPBlocks.POLISHED_LIMESTONE.getKey());

        tag(BlockTags.NEEDS_IRON_TOOL).add(PPBlocks.PIPE_REMNANT.getKey());
    }
}
