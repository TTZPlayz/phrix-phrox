package com.ttzplayz.phrixphrox.client;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.data.PPAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;

public class CursedFlames {

    public static final int CURSE_GREEN = 0xD9FF63;

    public static final SpriteId CURSED_FIRE_0 =
            Sheets.BLOCKS_MAPPER.apply(Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "cursed_fire_0"));
    public static final SpriteId CURSED_FIRE_1 =
            Sheets.BLOCKS_MAPPER.apply(Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "cursed_fire_1"));

    public static final ContextKey<Boolean> KEY = new ContextKey<>(
            Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "cursed_flames"));

    public static boolean isCursed(EntityRenderState state) {
        return Boolean.TRUE.equals(state.getRenderData(KEY));
    }

    public static boolean isLocalCursed() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && PPAttachments.hasCursedFlames(player);
    }

    public static TextureAtlasSprite sprite(SpriteId id) {
        return Minecraft.getInstance().getAtlasManager().get(id);
    }
}
