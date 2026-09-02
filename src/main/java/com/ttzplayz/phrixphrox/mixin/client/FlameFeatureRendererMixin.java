package com.ttzplayz.phrixphrox.mixin.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ttzplayz.phrixphrox.client.CursedFlames;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(FlameFeatureRenderer.class)
public class FlameFeatureRendererMixin {

    @Unique
    private static int phrixphrox$flameTint = CursedFlames.NORMAL_TINT;

    @Inject(method = "prepare", at = @At("HEAD"))
    private void phrixphrox$readTint(FlameFeatureRenderer.Submit submit, VertexConsumer buffer,
                                     TextureAtlasSprite fire1, TextureAtlasSprite fire2,
                                     CallbackInfo callback) {
        phrixphrox$flameTint = CursedFlames.isCursed(submit.entityRenderState())
                ? CursedFlames.CURSED_TINT
                : CursedFlames.NORMAL_TINT;
    }

    @ModifyConstant(method = "fireVertex", constant = @Constant(intValue = CursedFlames.NORMAL_TINT))
    private static int phrixphrox$tintFlame(int original) {
        return phrixphrox$flameTint;
    }
}
