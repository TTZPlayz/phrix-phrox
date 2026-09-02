package com.ttzplayz.phrixphrox.client;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.data.PPAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;

public class CursedFlames {

    public static final int NORMAL_TINT = -1;
    public static final int CURSED_TINT = 0xFF33FF4D;

    public static final int OVERLAY_TINT = -436207617;
    public static final int CURSED_OVERLAY_TINT = 0xE633FF4D;

    public static final ContextKey<Boolean> KEY = new ContextKey<>(
            Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "cursed_flames"));

    public static boolean isCursed(EntityRenderState state) {
        return Boolean.TRUE.equals(state.getRenderData(KEY));
    }

    public static int localOverlayTint(int original) {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && PPAttachments.hasCursedFlames(player) ? CURSED_OVERLAY_TINT : original;
    }
}
