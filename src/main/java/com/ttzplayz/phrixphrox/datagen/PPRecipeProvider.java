package com.ttzplayz.phrixphrox.datagen;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.blocks.PPBlocks;
import com.ttzplayz.phrixphrox.items.PPItems;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

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

        pewterSmithing(Items.IRON_SWORD, RecipeCategory.COMBAT, PPItems.PEWTER_SWORD.get());
        pewterSmithing(Items.IRON_SPEAR, RecipeCategory.COMBAT, PPItems.PEWTER_SPEAR.get());
        pewterSmithing(Items.IRON_AXE, RecipeCategory.TOOLS, PPItems.PEWTER_AXE.get());
        pewterSmithing(Items.IRON_PICKAXE, RecipeCategory.TOOLS, PPItems.PEWTER_PICKAXE.get());
        pewterSmithing(Items.IRON_SHOVEL, RecipeCategory.TOOLS, PPItems.PEWTER_SHOVEL.get());
        pewterSmithing(Items.IRON_HOE, RecipeCategory.TOOLS, PPItems.PEWTER_HOE.get());

        // Duplicate the template: 1 template + 7 diamonds + 1 lead ingot -> 2 templates
        shaped(RecipeCategory.MISC, PPItems.PEWTER_UPGRADE_TEMPLATE.get())
                .pattern("PLP")
                .pattern("PUP")
                .pattern("PPP")
                .define('P', PPItems.PEWTER_INGOT)
                .define('L', PPItems.LEAD_INGOT)
                .define('U', PPItems.PEWTER_UPGRADE_TEMPLATE)
                .unlockedBy(getHasName(PPBlocks.LIMESTONE.get()), has(PPBlocks.LIMESTONE))
                .save(output);
    }

    private void pewterSmithing(Item base, RecipeCategory category, Item result) {
        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(PPItems.PEWTER_UPGRADE_TEMPLATE),
                Ingredient.of(base),
                Ingredient.of(PPItems.PEWTER_INGOT),
                category,
                result
            )
            .unlocks(getHasName(PPItems.PEWTER_UPGRADE_TEMPLATE.get()), has(PPItems.PEWTER_UPGRADE_TEMPLATE))
            // Explicit namespace: a bare path would be parsed as "minecraft:"
            .save(output, PhrixPhrox.MOD_ID + ":" + getItemName(result) + "_smithing");
    }
}
