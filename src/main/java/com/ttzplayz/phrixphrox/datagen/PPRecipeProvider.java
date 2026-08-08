package com.ttzplayz.phrixphrox.datagen;

import com.ttzplayz.phrixphrox.blocks.PPBlocks;
import com.ttzplayz.phrixphrox.items.PPItems;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;

public class PPRecipeProvider extends RecipeProvider {

    public PPRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new PPRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Phrix, Phrox recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.TOOLS, PPItems.CURSED_STYLUS.get())
            .pattern("A  ")
            .pattern(" B ")
            .pattern("  C")
            .define('A', Items.GOLD_INGOT)
            .define('B', PPItems.LEAD_SCRAP)
            .define('C', Items.STICK)
            .unlockedBy(getHasName(PPItems.LEAD_SCRAP.get()), has(PPItems.LEAD_SCRAP))
            .save(output);

        shaped(RecipeCategory.MISC, PPItems.LEAD_TABLET.get())
            .pattern("AA")
            .define('A', PPItems.LEAD_INGOT)
            .unlockedBy(getHasName(PPItems.LEAD_INGOT.get()), has(PPItems.LEAD_INGOT))
            .save(output);

        shaped(RecipeCategory.MISC, PPBlocks.POLISHED_LIMESTONE.get())
                .pattern("LL")
                .pattern("LL")
                .define('L', PPBlocks.LIMESTONE)
                .unlockedBy(getHasName(PPBlocks.LIMESTONE.get()), has(PPBlocks.LIMESTONE))
                .save(output);

        oreSmelting(Arrays.asList(PPBlocks.PIPE_REMNANT), RecipeCategory.MISC, CookingBookCategory.MISC, PPItems.LEAD_SCRAP.get(), 0.25f, 100, "lead");
        oreBlasting(Arrays.asList(PPBlocks.PIPE_REMNANT), RecipeCategory.MISC, CookingBookCategory.MISC, PPItems.LEAD_SCRAP.get(), 0.25f, 100, "lead");
    }
}
