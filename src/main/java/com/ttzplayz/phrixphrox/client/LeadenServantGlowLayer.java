package com.ttzplayz.phrixphrox.client;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class LeadenServantGlowLayer extends EyesLayer<LivingEntityRenderState, LeadenServantModel> {

    private static final RenderType GLOW = RenderTypes.eyes(
            Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "textures/entity/leaden_servant_glow.png"));

    public LeadenServantGlowLayer(RenderLayerParent<LivingEntityRenderState, LeadenServantModel> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return GLOW;
    }
}
