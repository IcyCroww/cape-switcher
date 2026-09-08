package com.icycrow.capeswitcher.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
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
        float size = baseSize * zoom;
        float entityScale = entity.getScale();
        Vector3f translation = new Vector3f(
                0.0f,
                entity.getHeight() / 2.0f + ENTITY_HEIGHT_OFFSET * entityScale,
                0.0f
        );

        float savedBodyYaw = entity.bodyYaw;
        float savedYaw = entity.getYaw();
        float savedPitch = entity.getPitch();
        float savedLastHeadYaw = entity.lastHeadYaw;
        float savedHeadYaw = entity.headYaw;

        entity.bodyYaw = 0.0f;
        entity.setYaw(0.0f);
        entity.setPitch(0.0f);
        entity.lastHeadYaw = 0.0f;
        entity.headYaw = 0.0f;

        context.enableScissor(x1, y1, x2, y2);
        try {
            InventoryScreen.drawEntity(context, x1, y1, x2, y2,
                    size / entityScale, translation, rotation, null, entity);
        } finally {
            context.disableScissor();
            entity.bodyYaw = savedBodyYaw;
            entity.setYaw(savedYaw);
            entity.setPitch(savedPitch);
            entity.lastHeadYaw = savedLastHeadYaw;
            entity.headYaw = savedHeadYaw;
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

