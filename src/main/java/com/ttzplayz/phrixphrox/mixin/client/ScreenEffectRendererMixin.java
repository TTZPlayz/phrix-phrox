package com.ttzplayz.phrixphrox.mixin.client;

import com.ttzplayz.phrixphrox.client.CursedFlames;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.resources.model.sprite.SpriteId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

    @ModifyArg(
            method = "submit",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/sprite/SpriteGetter;get(Lnet/minecraft/client/resources/model/sprite/SpriteId;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"))
    private SpriteId phrixphrox$cursedFireOverlay(SpriteId original) {
        return CursedFlames.isLocalCursed() ? CursedFlames.CURSED_FIRE_1 : original;
    }
}
