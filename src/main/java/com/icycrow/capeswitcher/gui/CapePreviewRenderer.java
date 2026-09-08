package com.icycrow.capeswitcher.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CapePreviewRenderer {
    private static final float ENTITY_HEIGHT_OFFSET = 0.0625f;

    /**
     * Renders a living entity with full yaw/pitch/roll/zoom control.
     * yawDeg: 0 = facing away (back view), 180 = facing camera.
     * pitchDeg: vertical tilt, clamped externally.
     * rollDeg: side tilt around the screen-facing axis.
     * zoom: scale multiplier (1.0 = default).
     */
    public static void renderRotatedEntity(DrawContext context, int x1, int y1, int x2, int y2,
                                           int baseSize, float yawDeg, float pitchDeg, float rollDeg, float zoom,
                                           LivingEntity entity) {
        Quaternionf rotation = new Quaternionf()
                .rotateZ((float) Math.PI + (float) Math.toRadians(rollDeg))
                .rotateX((float) Math.toRadians(pitchDeg))
                .rotateY((float) Math.toRadians(yawDeg));

        EntityRenderState renderState = MinecraftClient.getInstance()
                .getEntityRenderDispatcher()
                .getAndUpdateRenderState(entity, 1.0f);
        renderState.light = 15728880;
        renderState.shadowPieces.clear();
        renderState.outlineColor = EntityRenderState.NO_OUTLINE;

        if (renderState instanceof LivingEntityRenderState livingState) {
            livingState.bodyYaw = 0.0f;
            livingState.relativeHeadYaw = 0.0f;
            livingState.pitch = 0.0f;

            if (livingState.baseScale != 0.0f) {
                livingState.width /= livingState.baseScale;
                livingState.height /= livingState.baseScale;
                livingState.baseScale = 1.0f;
            }
        }

        float size = Math.max(1.0f, baseSize * zoom);
        Vector3f translation = new Vector3f(
                0.0f,
                renderState.height / 2.0f + ENTITY_HEIGHT_OFFSET,
                0.0f
        );

        context.enableScissor(x1, y1, x2, y2);
        try {
            context.addEntity(renderState, size, translation, rotation, null, x1, y1, x2, y2);
        } finally {
            context.disableScissor();
        }
    }

    /**
     * Renders a flat cape texture preview.
     */
    public static void renderCapeTexture(DrawContext context, Identifier textureId,
                                         int x, int y, int displayWidth, int displayHeight) {
        if (textureId == null) return;

        context.drawTexture(RenderPipelines.GUI_TEXTURED, textureId,
                x, y, 0.0f, 0.0f, displayWidth, displayHeight, displayWidth, displayHeight);
    }
}

