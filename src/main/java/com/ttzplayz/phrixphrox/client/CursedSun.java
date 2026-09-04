package com.ttzplayz.phrixphrox.client;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.curse.PPEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CursedSun {

    public static final int SUN_TINT = CursedFlames.CURSE_GREEN;
    public static final int SKY_LIGHT_TINT = CursedFlames.CURSE_GREEN;
    public static final int SUNRISE_SUNSET_TINT = CursedFlames.CURSE_GREEN;

    public static final float SUN_HEIGHT = 100.0F;
    public static final float SUN_QUAD_SIZE = 30.0F;
    public static final float SUN_TEXTURE_PIXELS = 32.0F;
    public static final float SUN_DISC_PIXELS = 14.0F;
    public static final float SUN_PIXEL_UNITS = SUN_QUAD_SIZE * 2.0F / SUN_TEXTURE_PIXELS;

    public static final float CURSED_SUN_SIZE = SUN_QUAD_SIZE;

    public static final float EYE_OVERHANG_PIXELS = 1.0F;
    public static final float EYE_SIZE = (SUN_DISC_PIXELS * 0.5F + EYE_OVERHANG_PIXELS) * SUN_PIXEL_UNITS;

    public static final float EYE_TEXTURE_ROWS = 16.0F;
    public static final float EYE_PIXEL_UNITS = EYE_SIZE * 2.0F / EYE_TEXTURE_ROWS;
    public static final float EYE_PUPIL_SHIFT_PIXELS = -0.5F;
    public static final float EYE_PUPIL_OFFSET = EYE_PUPIL_SHIFT_PIXELS * EYE_PIXEL_UNITS;

    public static final Identifier CURSED_SUN_TEXTURE =
            Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "textures/environment/cursed_sun.png");
    public static final Identifier SUN_EYE_TEXTURE =
            Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "textures/environment/sun_eye.png");

    public static boolean isEscalated() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null
                && player.hasEffect(PPEffects.SUN_BURNING)
                && PPEffects.isEscalated(player, PPEffects.SUN_BURNING);
    }

    public static boolean isDaytime() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && player.level().isBrightOutside();
    }

    public static boolean tintsSkyLight() {
        return isEscalated() && isDaytime();
    }

    public static int sunriseAndSunsetTint(int original) {
        return ARGB.color(ARGB.alpha(original), SUNRISE_SUNSET_TINT);
    }

    public static Vector4f greenTint(float alpha) {
        Vector3f tint = ARGB.vector3fFromRGB24(SUN_TINT);
        return new Vector4f(tint.x, tint.y, tint.z, alpha);
    }

    public static Vector4f plainTint(float alpha) {
        return new Vector4f(1.0F, 1.0F, 1.0F, alpha);
    }

    public static Vector4f maskTint(float rainBrightness) {
        return new Vector4f(rainBrightness, rainBrightness, rainBrightness, 1.0F);
    }

    public static Vector3f skyLightTint() {
        return ARGB.vector3fFromRGB24(SKY_LIGHT_TINT);
    }
}
