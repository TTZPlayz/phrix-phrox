package com.ttzplayz.phrixphrox;

import com.ttzplayz.phrixphrox.menu.PPMenuTypes;
import com.ttzplayz.phrixphrox.menu.WritingDeskScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = PhrixPhrox.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = PhrixPhrox.MOD_ID, value = Dist.CLIENT)
public class PhrixPhroxClient {

    public PhrixPhroxClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(
            IConfigScreenFactory.class,
            ConfigurationScreen::new
        );
    }

    @SubscribeEvent
    static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(PPMenuTypes.WRITING_DESK_MENU.get(), WritingDeskScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        PhrixPhrox.LOGGER.info("HELLO FROM CLIENT SETUP");
        PhrixPhrox.LOGGER.info(
            "MINECRAFT NAME >> {}",
            Minecraft.getInstance().getUser().getName()
        );
    }
}
