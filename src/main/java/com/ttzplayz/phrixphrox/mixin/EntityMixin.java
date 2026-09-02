package com.ttzplayz.phrixphrox.mixin;

import com.ttzplayz.phrixphrox.curse.sun_burning.SunBurningCurse;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "clearFire", at = @At("HEAD"), cancellable = true)
    private void phrixphrox$refuseDousing(CallbackInfo callback) {
        Entity self = (Entity) (Object) this;
        if (self.level().isClientSide() || !SunBurningCurse.resistsDousing(self)) return;

        SunBurningCurse.noticeDousing(self);
        callback.cancel();
    }
}
