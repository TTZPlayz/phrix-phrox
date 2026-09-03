package com.ttzplayz.phrixphrox;

import com.ttzplayz.phrixphrox.block.entity.PPBlockEntities;
import com.ttzplayz.phrixphrox.client.CursedFlames;
import com.ttzplayz.phrixphrox.client.LeadenServantModel;
import com.ttzplayz.phrixphrox.client.LeadenServantRenderer;
import com.ttzplayz.phrixphrox.entity.PPEntities;
import com.ttzplayz.phrixphrox.data.PPAttachments;
import com.ttzplayz.phrixphrox.client.WritingDeskRenderer;
import com.ttzplayz.phrixphrox.menu.PPMenuTypes;
import com.ttzplayz.phrixphrox.particle.PPParticles;
import com.ttzplayz.phrixphrox.menu.WritingDeskScreen;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.platform.BlendFactor;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.renderstate.AvatarRenderStateModifier;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = PhrixPhrox.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = PhrixPhrox.MOD_ID, value = Dist.CLIENT)
public class PhrixPhroxClient {

    public static final RenderPipeline SUN_EYE_PIPELINE = RenderPipelines.CELESTIAL.toBuilder()
            .withLocation(Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "pipeline/sun_eye"))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .build();

    public static final RenderPipeline CURSED_SUN_MASK_PIPELINE = RenderPipelines.CELESTIAL.toBuilder()
            .withLocation(Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "pipeline/cursed_sun_mask"))
            .withColorTargetState(new ColorTargetState(new BlendFunction(
                    BlendFactor.ZERO, BlendFactor.ONE_MINUS_SRC_COLOR, BlendFactor.ONE, BlendFactor.ZERO)))
            .build();

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
    static void onRegisterRenderPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(SUN_EYE_PIPELINE);
        event.registerPipeline(CURSED_SUN_MASK_PIPELINE);
    }

    @SubscribeEvent
    static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(PPMenuTypes.WRITING_DESK_MENU.get(), WritingDeskScreen::new);
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(PPBlockEntities.WRITING_DESK_BE.get(), WritingDeskRenderer::new);
        event.registerEntityRenderer(PPEntities.LEADEN_SERVANT.get(), LeadenServantRenderer::new);
    }

    @SubscribeEvent
    static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LeadenServantModel.LAYER, LeadenServantModel::createBodyLayer);
    }

    @SubscribeEvent
    static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(PPParticles.CURSE_FLAME.get(), FlameParticle.Provider::new);
    }

    @SubscribeEvent
    static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerAvatarEntityModifier(new AvatarRenderStateModifier() {
            @Override
            public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState renderState) {
                renderState.setRenderData(CursedFlames.KEY, PPAttachments.hasCursedFlames(avatar));
            }
        });
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
