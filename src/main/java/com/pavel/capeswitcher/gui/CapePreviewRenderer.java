package com.pavel.capeswitcher.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public class CapePreviewRenderer {

    /**
     * Renders a living entity with full yaw/pitch/zoom control.
     * yawDeg: 0 = facing camera, 180 = facing away (back view).
     * pitchDeg: vertical tilt, clamped externally.
     * zoom: scale multiplier (1.0 = default).
     */
    public static void renderRotatedEntity(DrawContext context, int x1, int y1, int x2, int y2,
                                           int baseSize, float yawDeg, float pitchDeg, float zoom,
                                           LivingEntity entity) {
        float centerX = (x1 + x2) / 2.0f;
        float centerY = (y1 + y2) / 2.0f;

        context.enableScissor(x1, y1, x2, y2);

        // Save entity rotation state
        float savedBodyYaw = entity.bodyYaw;
        float savedYaw = entity.getYaw();
        float savedPitch = entity.getPitch();
        float savedHeadYaw = entity.headYaw;
        float savedPrevHeadYaw = entity.prevHeadYaw;
        float savedPrevBodyYaw = entity.prevBodyYaw;

        entity.bodyYaw = 0;
        entity.setYaw(0);
        entity.setPitch(0);
        entity.headYaw = 0;
        entity.prevHeadYaw = 0;
        entity.prevBodyYaw = 0;

        float scaledSize = baseSize * zoom;

        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(centerX, centerY, 50.0);
        matrices.scale(scaledSize, scaledSize, -scaledSize);

        Quaternionf rotation = new Quaternionf()
                .rotateZ((float) Math.PI)
                .rotateX((float) Math.toRadians(pitchDeg))
                .rotateY((float) Math.toRadians(yawDeg));
        matrices.multiply(rotation);

        DiffuseLighting.enableGuiDepthLighting();

        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        dispatcher.setRenderShadows(false);

        VertexConsumerProvider.Immediate immediate =
                MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        dispatcher.render(entity, 0.0, -entity.getHeight() / 2.0, 0.0,
                1.0f, matrices, immediate, 15728880);
        immediate.draw();
        dispatcher.setRenderShadows(true);

        matrices.pop();
        DiffuseLighting.enableGuiDepthLighting();

        // Restore entity rotation state
        entity.bodyYaw = savedBodyYaw;
        entity.setYaw(savedYaw);
        entity.setPitch(savedPitch);
        entity.headYaw = savedHeadYaw;
        entity.prevHeadYaw = savedPrevHeadYaw;
        entity.prevBodyYaw = savedPrevBodyYaw;

        context.disableScissor();
    }

    /**
     * Renders a flat cape texture preview.
     */
    public static void renderCapeTexture(DrawContext context, Identifier textureId,
                                         int x, int y, int displayWidth, int displayHeight) {
        if (textureId == null) return;

        context.drawTexture(RenderLayer::getGuiTextured, textureId,
                x, y, 0.0f, 0.0f, displayWidth, displayHeight, displayWidth, displayHeight);
    }
}
