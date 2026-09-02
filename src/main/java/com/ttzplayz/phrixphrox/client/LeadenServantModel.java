package com.ttzplayz.phrixphrox.client;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class LeadenServantModel extends EntityModel<LivingEntityRenderState> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "leaden_servant"), "main");

    private static final int TEXTURE_WIDTH = 128;
    private static final int TEXTURE_HEIGHT = 128;

    private static final float MILLIS_PER_TICK = 50.0F;

    private static final float HOVER_AMPLITUDE = 1.6F;
    private static final float HOVER_PERIOD_TICKS = 70.0F;

    private final ModelPart body;
    private final ModelPart eye;
    private final KeyframeAnimation idle;

    public LeadenServantModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.eye = this.body.getChild("eyeofdoomanddespair");
        this.eye.visible = false;
        this.idle = LeadenServantAnimation.IDLE.bake(root);
    }

    public void setEyeVisible(boolean visible) {
        this.eye.visible = visible;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();

        PartDefinition body = parts.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-16.0F, -8.0F, 0.01F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)),
                PartPose.offset(8.0F, 16.0F, -8.01F));

        body.addOrReplaceChild("mask1", CubeListBuilder.create()
                        .texOffs(34, 48)
                        .addBox(-9.0F, -8.0F, -0.01F, 9.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.01F));

        body.addOrReplaceChild("wing2", CubeListBuilder.create()
                        .texOffs(32, 32)
                        .addBox(-16.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-16.0F, 0.0F, 8.01F));

        body.addOrReplaceChild("wing1", CubeListBuilder.create()
                        .texOffs(0, 32)
                        .addBox(0.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 8.01F));

        body.addOrReplaceChild("mask2", CubeListBuilder.create()
                        .texOffs(52, 48)
                        .addBox(0.0F, -8.0F, 0.0F, 8.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-16.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("eyeofdoomanddespair", CubeListBuilder.create()
                        .texOffs(0, 48)
                        .addBox(-16.5F, -3.5F, 0.0F, 17.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        super.setupAnim(state);
        this.idle.apply((long) (state.ageInTicks * MILLIS_PER_TICK), 1.0F);
        this.body.y += Mth.sin(state.ageInTicks / HOVER_PERIOD_TICKS * Mth.TWO_PI) * HOVER_AMPLITUDE;
    }
}
