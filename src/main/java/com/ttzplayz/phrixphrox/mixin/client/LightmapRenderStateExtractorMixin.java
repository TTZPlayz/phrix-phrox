package com.ttzplayz.phrixphrox.mixin.client;

import com.ttzplayz.phrixphrox.client.CursedSun;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapRenderStateExtractor.class)
public class LightmapRenderStateExtractorMixin {

    @Inject(method = "extract", at = @At("RETURN"))
    private void phrixphrox$tintSkyLight(LightmapRenderState renderState, float partialTicks, CallbackInfo callback) {
        if (!renderState.needsUpdate) return;
        if (!CursedSun.tintsSkyLight()) return;

        renderState.skyLightColor = CursedSun.skyLightTint();
    }
}
