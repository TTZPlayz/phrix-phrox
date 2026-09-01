package com.ttzplayz.phrixphrox.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class CurseFlameParticle extends FlameParticle {

    private static final float R = 0.45f;
    private static final float G = 0.90f;
    private static final float B = 0.30f;

    protected CurseFlameParticle(ClientLevel level, double x, double y, double z,
                                 double xd, double yd, double zd, TextureAtlasSprite sprite) {
        super(level, x, y, z, xd, yd, zd, sprite);
        this.setColor(R, G, B);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level,
                                       double x, double y, double z,
                                       double xAux, double yAux, double zAux, RandomSource random) {
            return new CurseFlameParticle(level, x, y, z, xAux, yAux, zAux, this.sprite.get(random));
        }
    }
}
