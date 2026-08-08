package com.ttzplayz.phrixphrox.items;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.blocks.PPBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PPCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PhrixPhrox.MOD_ID);

    public static final Supplier<CreativeModeTab> PHRIX_PHROX = CREATIVE_MODE_TABS.register("phrix_phrox",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(PPItems.DEFIXION.get()))
                    .title(Component.translatable("itemGroup.phrix_phrox"))
                    .displayItems((itemDisplayParameters, output) -> {
                        for (DeferredItem<Item> item : PPItems.ALL_ITEMS) {
                            output.accept(item);
                        }

                        for (DeferredBlock<Block> block : PPBlocks.ALL_BLOCKS) {
                            output.accept(block);
                        }

                    }).build());
}
