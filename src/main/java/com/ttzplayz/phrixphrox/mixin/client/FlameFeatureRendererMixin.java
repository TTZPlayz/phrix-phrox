package com.ttzplayz.phrixphrox.mixin.client;

import com.ttzplayz.phrixphrox.client.CursedFlames;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FlameFeatureRenderer.class)
public class FlameFeatureRendererMixin {

    @ModifyArgs(
            method = "buildGroup",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/feature/FlameFeatureRenderer;prepare(Lnet/minecraft/client/renderer/feature/FlameFeatureRenderer$Submit;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
    private void phrixphrox$cursedFireSprites(Args args) {
        FlameFeatureRenderer.Submit submit = args.get(0);
        if (!CursedFlames.isCursed(submit.entityRenderState())) return;

        args.set(2, CursedFlames.sprite(CursedFlames.CURSED_FIRE_0));
        args.set(3, CursedFlames.sprite(CursedFlames.CURSED_FIRE_1));
    }
}
