package com.ttzplayz.phrixphrox.mixin.client;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.ttzplayz.phrixphrox.PhrixPhroxClient;
import com.ttzplayz.phrixphrox.client.CursedSun;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.OptionalDouble;

@Mixin(SkyRenderer.class)
public class SkyRendererMixin {

    @Shadow
    @Final
    private RenderTarget renderTarget;

    @Unique
    private GpuBuffer phrixphrox$cursedQuad;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void phrixphrox$buildCursedQuad(CallbackInfo callback) {
        VertexFormat format = DefaultVertexFormat.POSITION_TEX;

        try (ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(4 * format.getVertexSize())) {
            BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.QUADS, format);
            bufferBuilder.addVertex(-1.0F, 0.0F, -1.0F).setUv(0.0F, 0.0F);
            bufferBuilder.addVertex(1.0F, 0.0F, -1.0F).setUv(1.0F, 0.0F);
            bufferBuilder.addVertex(1.0F, 0.0F, 1.0F).setUv(1.0F, 1.0F);
            bufferBuilder.addVertex(-1.0F, 0.0F, 1.0F).setUv(0.0F, 1.0F);

            try (MeshData mesh = bufferBuilder.buildOrThrow()) {
                this.phrixphrox$cursedQuad = RenderSystem.getDevice()
                        .createBuffer(() -> "Cursed sun quad", 32, mesh.vertexBuffer());
            }
        }
    }

    @Inject(method = "renderSun", at = @At("HEAD"), cancellable = true)
    private void phrixphrox$renderCursedSun(float rainBrightness, PoseStack poseStack, CallbackInfo callback) {
        if (!CursedSun.isEscalated()) return;

        this.phrixphrox$drawCelestial(poseStack, PhrixPhroxClient.CURSED_SUN_MASK_PIPELINE,
                CursedSun.CURSED_SUN_TEXTURE, CursedSun.CURSED_SUN_SIZE, 0.0F,
                CursedSun.maskTint(rainBrightness), "Cursed sun mask");
        this.phrixphrox$drawCelestial(poseStack, RenderPipelines.CELESTIAL,
                CursedSun.CURSED_SUN_TEXTURE, CursedSun.CURSED_SUN_SIZE, 0.0F,
                CursedSun.greenTint(rainBrightness), "Cursed sun");
        this.phrixphrox$drawCelestial(poseStack, PhrixPhroxClient.SUN_EYE_PIPELINE,
                CursedSun.SUN_EYE_TEXTURE, CursedSun.EYE_SIZE, CursedSun.EYE_PUPIL_OFFSET,
                CursedSun.plainTint(rainBrightness), "Cursed sun eye");

        callback.cancel();
    }

    @Unique
    private void phrixphrox$drawCelestial(PoseStack poseStack, RenderPipeline pipeline, Identifier texture,
                                          float size, float offset, Vector4f tint, String pass) {
        AbstractTexture bound = Minecraft.getInstance().getTextureManager().getTexture(texture);
        RenderSystem.AutoStorageIndexBuffer quadIndices = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);

        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.mul(poseStack.last().pose());
        modelViewStack.translate(0.0F, CursedSun.SUN_HEIGHT, offset);
        modelViewStack.scale(size, 1.0F, size);
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(new Matrix4f(modelViewStack), tint);
        GpuTextureView color = this.renderTarget.getColorTextureView();
        GpuTextureView depth = this.renderTarget.getDepthTextureView();

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> pass, color, Optional.empty(), depth, OptionalDouble.empty())) {
            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.bindTexture("Sampler0", bound.getTextureView(), bound.getSampler());
            renderPass.setVertexBuffer(0, this.phrixphrox$cursedQuad.slice());
            renderPass.setIndexBuffer(quadIndices.getBuffer(6), quadIndices.type());
            renderPass.drawIndexed(6, 1, 0, 0, 0);
        }

        modelViewStack.popMatrix();
    }

    @Inject(method = "close", at = @At("RETURN"))
    private void phrixphrox$closeCursedQuad(CallbackInfo callback) {
        if (this.phrixphrox$cursedQuad != null) {
            this.phrixphrox$cursedQuad.close();
        }
    }
}
