package com.ttzplayz.phrixphrox.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ttzplayz.phrixphrox.block.WritingDeskBlock;
import com.ttzplayz.phrixphrox.block.entity.WritingDeskBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class WritingDeskRenderer implements BlockEntityRenderer<WritingDeskBlockEntity, WritingDeskRenderer.State> {

    private static final float PANEL_X = 0.5f;
    private static final float PANEL_Y = 0.906f;
    private static final float PANEL_Z = 0.469f;
    private static final float PANEL_TILT = -22.5f;
    private static final float SCALE = 0.55f;

    public static class State extends BlockEntityRenderState {
        public Direction facing = Direction.NORTH;
        public final ItemStackRenderState tablet = new ItemStackRenderState();
    }

    private final ItemModelResolver itemModelResolver;

    public WritingDeskRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(WritingDeskBlockEntity blockEntity, State state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        BlockState blockState = blockEntity.getBlockState();
        state.facing = blockState.hasProperty(WritingDeskBlock.FACING)
                ? blockState.getValue(WritingDeskBlock.FACING)
                : Direction.NORTH;

        ItemStack tablet = blockEntity.inventory
                .getResource(WritingDeskBlockEntity.SLOT_TABLET)
                .toStack(blockEntity.inventory.getAmountAsInt(WritingDeskBlockEntity.SLOT_TABLET));
        itemModelResolver.updateForTopItem(state.tablet, tablet, ItemDisplayContext.FIXED,
                blockEntity.getLevel(), null, (int) blockEntity.getBlockPos().asLong());
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.tablet.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(PANEL_X, PANEL_Y, PANEL_Z);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.getOpposite().toYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0f + PANEL_TILT));
        poseStack.scale(SCALE, SCALE, SCALE);
        state.tablet.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
