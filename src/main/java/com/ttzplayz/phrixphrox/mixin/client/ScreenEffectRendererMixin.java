package com.ttzplayz.phrixphrox.mixin.client;

import com.ttzplayz.phrixphrox.client.CursedFlames;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

    @ModifyConstant(method = "buildFireQuad", constant = @Constant(intValue = CursedFlames.OVERLAY_TINT))
    private static int phrixphrox$tintOverlay(int original) {
        return CursedFlames.localOverlayTint(original);
    }
}
