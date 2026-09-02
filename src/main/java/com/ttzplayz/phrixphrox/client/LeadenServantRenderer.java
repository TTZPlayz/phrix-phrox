package com.ttzplayz.phrixphrox.client;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.entity.LeadenServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class LeadenServantRenderer extends MobRenderer<LeadenServant, LivingEntityRenderState, LeadenServantModel> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "textures/entity/leaden_servant.png");

    private static final float SHADOW_RADIUS = 0.5F;

    public LeadenServantRenderer(EntityRendererProvider.Context context) {
        super(context, new LeadenServantModel(context.bakeLayer(LeadenServantModel.LAYER)), SHADOW_RADIUS);
        this.addLayer(new LeadenServantGlowLayer(this));
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }
}
