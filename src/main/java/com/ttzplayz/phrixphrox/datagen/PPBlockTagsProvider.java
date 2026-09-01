package com.ttzplayz.phrixphrox.datagen;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.block.PPBlocks;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.registries.DeferredBlock;

public class PPBlockTagsProvider extends BlockTagsProvider {

    public PPBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, PhrixPhrox.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (DeferredBlock<Block> block : PPBlocks.ALL_BLOCKS) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.getKey());
        }
        tag(BlockTags.NEEDS_IRON_TOOL).add(PPBlocks.PIPE_REMNANT.getKey());
    }
}
