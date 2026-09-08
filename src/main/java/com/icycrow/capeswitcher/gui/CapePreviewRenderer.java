package com.icycrow.capeswitcher.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
        Quaternionf rotation = new Quaternionf()
                .rotateX((float) Math.toRadians(pitchDeg))
                .rotateY((float) Math.toRadians(yawDeg));

        context.enableScissor(x1, y1, x2, y2);
        InventoryScreen.drawEntity(context, x1, y1, x2, y2,
                baseSize * zoom, new Vector3f(), rotation, null, entity);
        context.disableScissor();
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

