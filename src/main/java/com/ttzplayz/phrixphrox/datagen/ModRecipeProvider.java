package com.ttzplayz.phrixphrox.datagen;

import com.ttzplayz.phrixphrox.blocks.ModBlocks;
import com.ttzplayz.phrixphrox.items.ModItems;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Phrix, Phrox recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.TOOLS, ModItems.CURSED_STYLUS.get())
            .pattern("A  ")
            .pattern(" B ")
            .pattern("  C")
            .define('A', Items.GOLD_INGOT)
            .define('B', ModItems.LEAD_INGOT)
            .define('C', Items.STICK)
            .unlockedBy(getHasName(ModItems.LEAD_INGOT.get()), has(ModItems.LEAD_INGOT))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.LEAD_TABLET.get())
            .pattern("AA ")
            .define('A', ModItems.LEAD_INGOT)
            .unlockedBy(getHasName(ModItems.LEAD_INGOT.get()), has(ModItems.LEAD_INGOT))
            .save(output);

        oreSmelting(Arrays.asList(ModBlocks.PIPE_REMNANT), RecipeCategory.MISC, CookingBookCategory.MISC, ModItems.LEAD_SCRAP.get(), 0.25f, 100, "lead");
        oreBlasting(Arrays.asList(ModBlocks.PIPE_REMNANT), RecipeCategory.MISC, CookingBookCategory.MISC, ModItems.LEAD_SCRAP.get(), 0.25f, 100, "lead");
    }
}
