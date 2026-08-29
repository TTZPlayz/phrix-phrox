package com.ttzplayz.phrixphrox;

import static net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
import static net.minecraft.world.item.Items.*;

import com.mojang.logging.LogUtils;
import com.ttzplayz.phrixphrox.blocks.PPBlocks;
import com.ttzplayz.phrixphrox.curse.PPEffects;
import com.ttzplayz.phrixphrox.data.PPData;
import com.ttzplayz.phrixphrox.items.PPCreativeModeTabs;
import com.ttzplayz.phrixphrox.items.PPItems;
import com.ttzplayz.phrixphrox.saveddata.PlayerCurseData;

import net.minecraft.world.item.CreativeModeTab;
import com.ttzplayz.phrixphrox.particle.PPParticles;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(PhrixPhrox.MOD_ID)
public class PhrixPhrox {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "phrixphrox";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public PhrixPhrox(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        PPItems.register(modEventBus);
        PPBlocks.register(modEventBus);
        PPData.register(modEventBus);
        PPEffects.register(modEventBus);
        PPParticles.register(modEventBus);

        PPCreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {}

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.insertAfter(DIAMOND_SWORD.getDefaultInstance(), PPItems.PEWTER_SWORD.toStack(), PARENT_AND_SEARCH_TABS);
            event.insertAfter(DIAMOND_AXE.getDefaultInstance(), PPItems.PEWTER_AXE.toStack(), PARENT_AND_SEARCH_TABS);
            event.insertAfter(DIAMOND_SPEAR.getDefaultInstance(), PPItems.PEWTER_SPEAR.toStack(), PARENT_AND_SEARCH_TABS);
        }

        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.insertAfter(DIAMOND_HOE.getDefaultInstance(), PPItems.PEWTER_SHOVEL.toStack(), PARENT_AND_SEARCH_TABS);
            event.insertAfter(PPItems.PEWTER_SHOVEL.toStack(), PPItems.PEWTER_PICKAXE.toStack(), PARENT_AND_SEARCH_TABS);
            event.insertAfter(PPItems.PEWTER_PICKAXE.toStack(), PPItems.PEWTER_AXE.toStack(), PARENT_AND_SEARCH_TABS);
            event.insertAfter(PPItems.PEWTER_AXE.toStack(), PPItems.PEWTER_HOE.toStack(), PARENT_AND_SEARCH_TABS);
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.insertAfter(CHISELED_TUFF_BRICKS.getDefaultInstance(), PPBlocks.LIMESTONE.toStack(), PARENT_AND_SEARCH_TABS);
            event.insertAfter(PPBlocks.LIMESTONE.toStack(), PPBlocks.POLISHED_LIMESTONE.toStack(), PARENT_AND_SEARCH_TABS);
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.insertAfter(ANCIENT_DEBRIS.getDefaultInstance(), PPBlocks.PIPE_REMNANT.toStack(), PARENT_AND_SEARCH_TABS);
            event.insertAfter(GOLD_INGOT.getDefaultInstance(), PPItems.LEAD_SCRAP.toStack(), PARENT_AND_SEARCH_TABS);
            event.insertAfter(RAW_GOLD.getDefaultInstance(), PPItems.PLUMBOUS_MIXTURE.toStack(), PARENT_AND_SEARCH_TABS);

            event.insertAfter(GOLD_INGOT.getDefaultInstance(), PPItems.LEAD_INGOT.toStack(), PARENT_AND_SEARCH_TABS);
            event.insertAfter(PPItems.LEAD_INGOT.toStack(), PPItems.PEWTER_INGOT.toStack(), PARENT_AND_SEARCH_TABS);

            event.insertAfter(NETHERITE_UPGRADE_SMITHING_TEMPLATE.getDefaultInstance(), PPItems.PEWTER_UPGRADE_TEMPLATE.toStack(), PARENT_AND_SEARCH_TABS);
        } //change to after gold? (for lead), not sure about pewter

        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.insertAfter(TUFF.getDefaultInstance(), PPBlocks.LIMESTONE.toStack(), PARENT_AND_SEARCH_TABS);
            event.insertAfter(ANCIENT_DEBRIS.getDefaultInstance(), PPBlocks.PIPE_REMNANT.toStack(), PARENT_AND_SEARCH_TABS);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        event.getServer().getDataStorage().computeIfAbsent(PlayerCurseData.ID);
    }
}
