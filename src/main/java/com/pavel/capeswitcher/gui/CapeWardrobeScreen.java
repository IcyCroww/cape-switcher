package com.pavel.capeswitcher.gui;

import com.pavel.capeswitcher.cape.CapeEntry;
import com.pavel.capeswitcher.cape.CapeManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CapeWardrobeScreen extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger("CapeSwitcher");

    // Layout constants
    private static final int TITLE_HEIGHT = 20;
    private static final int BOTTOM_BAR_HEIGHT = 52;
    private static final int CARD_HEIGHT = 32;
    private static final int CARD_GAP = 2;
    private static final int CARD_PADDING = 4;
    private static final int THUMB_SIZE = 24;
    private static final int PANEL_GAP = 8;

    // Colors
    private static final int COLOR_PANEL_BG = 0x80000000;
    private static final int COLOR_CARD_BG = 0xFF2A2A2A;
    private static final int COLOR_CARD_HOVER = 0xFF3A3A3A;
    private static final int COLOR_CARD_SELECTED = 0xFF1A4A1A;
    private static final int COLOR_CARD_SELECTED_HOVER = 0xFF2A5A2A;
    private static final int COLOR_CARD_BORDER = 0xFF555555;
    private static final int COLOR_CARD_SELECTED_BORDER = 0xFF44AA44;
    private static final int COLOR_TEXT = 0xFFFFFF;
    private static final int COLOR_TEXT_DIM = 0xAAAAAA;
    private static final int COLOR_PLACEHOLDER = 0xFF444444;

    // Scroll state (cape list)
    private int scrollOffset = 0;
    private int maxScroll = 0;

    // Hover state
    private int hoveredCardIndex = -1;

    // Layout regions (computed in init)
    private int leftPanelX, leftPanelY, leftPanelW, leftPanelH;
    private int rightPanelX, rightPanelY, rightPanelW, rightPanelH;
    private int cardListY, cardListH;

    // 3D preview rotation/zoom state
    private static final float DEFAULT_YAW = 0.0f;
    private static final float DEFAULT_PITCH = 0.0f;
    private static final float DEFAULT_ZOOM = 1.0f;
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 3.0f;
    private static final float MAX_PITCH = 45.0f;
    private static final float ROTATION_SENSITIVITY = 0.8f;
    private static final float PITCH_SENSITIVITY = 0.5f;
    private static final float ZOOM_SENSITIVITY = 0.15f;

    private float previewYaw = DEFAULT_YAW;
    private float previewPitch = DEFAULT_PITCH;
    private float previewZoom = DEFAULT_ZOOM;
    private boolean draggingPreview = false;
    private double lastMouseX;
    private double lastMouseY;

    public CapeWardrobeScreen() {
        super(Text.translatable("screen.capeswitcher.title"));
    }

    @Override
    protected void init() {
        super.init();

        CapeManager manager = CapeManager.getInstance();
        if (manager == null) return;

        // Reload capes on screen open
        manager.reloadCapes();

        // Compute layout regions
        int contentY = TITLE_HEIGHT + 4;
        int contentH = this.height - contentY - BOTTOM_BAR_HEIGHT - 4;

        leftPanelW = (int) (this.width * 0.38);
        leftPanelX = PANEL_GAP;
        leftPanelY = contentY;
        leftPanelH = contentH;

        rightPanelX = leftPanelX + leftPanelW + PANEL_GAP;
        rightPanelY = contentY;
        rightPanelW = this.width - rightPanelX - PANEL_GAP;
        rightPanelH = contentH;

        cardListY = rightPanelY + 4;
        cardListH = rightPanelH - 8;

        // Compute scroll bounds
        List<CapeEntry> capes = manager.getCapes();
        int totalCardsHeight = capes.size() * (CARD_HEIGHT + CARD_GAP);
        maxScroll = Math.max(0, totalCardsHeight - cardListH);
        scrollOffset = Math.min(scrollOffset, maxScroll);

        // Bottom buttons
        int btnW = 80;
        int btnH = 20;
        int btnY1 = this.height - BOTTOM_BAR_HEIGHT + 4;
        int totalBtnWidth = btnW * 5 + 4 * 4;
        int btnStartX = (this.width - totalBtnWidth) / 2;

        // Row: Account | None | Reload | Open Folder | Done
        addDrawableChild(ButtonWidget.builder(
                Text.translatable("button.capeswitcher.account"),
                button -> {
                    manager.setModeAccount();
                    rebuildScreen();
                }
        ).dimensions(btnStartX, btnY1, btnW, btnH).build());

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("button.capeswitcher.none"),
                button -> {
                    manager.setModeNone();
                    rebuildScreen();
                }
        ).dimensions(btnStartX + btnW + 4, btnY1, btnW, btnH).build());

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("button.capeswitcher.reload"),
                button -> {
                    manager.reloadCapes();
                    scrollOffset = 0;
                    rebuildScreen();
                }
        ).dimensions(btnStartX + (btnW + 4) * 2, btnY1, btnW, btnH).build());

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("button.capeswitcher.open_folder"),
                button -> {
                    try {
                        Util.getOperatingSystem().open(manager.getCapesDir().toFile());
                    } catch (Exception e) {
                        LOGGER.error("Failed to open capes folder", e);
                    }
                }
        ).dimensions(btnStartX + (btnW + 4) * 3, btnY1, btnW, btnH).build());

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("button.capeswitcher.done"),
                button -> close()
        ).dimensions(btnStartX + (btnW + 4) * 4, btnY1, btnW, btnH).build());

        // Reset View button (small, bottom-right corner of preview panel)
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Reset"),
                button -> {
                    previewYaw = DEFAULT_YAW;
                    previewPitch = DEFAULT_PITCH;
                    previewZoom = DEFAULT_ZOOM;
                }
        ).dimensions(leftPanelX + leftPanelW - 44, leftPanelY + leftPanelH - 18, 40, 14).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        CapeManager manager = CapeManager.getInstance();
        if (manager == null) return;

        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 6, COLOR_TEXT);

        // Current mode indicator
        String mode = manager.getConfig().getCapeMode();
        String modeLabel = switch (mode) {
            case "local" -> {
                CapeEntry current = manager.getCurrentCape();
                yield current != null ? "Mode: Local (" + current.getDisplayName() + ")" : "Mode: Local";
            }
            case "none" -> "Mode: None";
            default -> "Mode: Account Cape";
        };
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(modeLabel),
                this.width / 2, this.height - BOTTOM_BAR_HEIGHT - 8, COLOR_TEXT_DIM);

        // Left panel background
        context.fill(leftPanelX, leftPanelY, leftPanelX + leftPanelW, leftPanelY + leftPanelH, COLOR_PANEL_BG);

        // Render player preview
        renderPlayerPreview(context, manager, mouseX, mouseY);

        // Right panel background
        context.fill(rightPanelX, rightPanelY, rightPanelX + rightPanelW, rightPanelY + rightPanelH, COLOR_PANEL_BG);

        // Render cape card list
        renderCapeList(context, manager, mouseX, mouseY);
    }

    private void renderPlayerPreview(DrawContext context, CapeManager manager, int mouseX, int mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // 3D player model - upper 75% of left panel
        int previewH = (int) (leftPanelH * 0.75);
        int modelSize = (int) (Math.min(leftPanelW, previewH) * 0.4f);

        CapePreviewRenderer.renderRotatedEntity(
                context,
                leftPanelX, leftPanelY,
                leftPanelX + leftPanelW, leftPanelY + previewH,
                modelSize, previewYaw, previewPitch, previewZoom,
                client.player
        );

        // Hint text at bottom of preview area
        if (isMouseOverPreview(mouseX, mouseY) && !draggingPreview) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("Drag to rotate • Scroll to zoom"),
                    leftPanelX + leftPanelW / 2,
                    leftPanelY + previewH - 10,
                    0x66FFFFFF);
        }

        // Cape name below model
        int textY = leftPanelY + previewH + 4;
        CapeEntry displayCape = manager.getPreviewCape();
        if (displayCape == null) {
            displayCape = manager.getCurrentCape();
        }

        if (displayCape != null) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal(displayCape.getDisplayName()),
                    leftPanelX + leftPanelW / 2, textY, COLOR_TEXT);

            // Flat cape texture preview below the name
            int texPreviewY = textY + 14;
            int texW = 64;
            int texH = 32;
            int texX = leftPanelX + (leftPanelW - texW) / 2;

            context.fill(texX - 1, texPreviewY - 1, texX + texW + 1, texPreviewY + texH + 1, COLOR_CARD_BORDER);
            CapePreviewRenderer.renderCapeTexture(context, displayCape.getTextureId(),
                    texX, texPreviewY, texW, texH);
        } else {
            String noCapeTxt = "account".equals(manager.getConfig().getCapeMode())
                    ? "Account Cape" : "No Cape";
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal(noCapeTxt),
                    leftPanelX + leftPanelW / 2, textY, COLOR_TEXT_DIM);
        }
    }

    private void renderCapeList(DrawContext context, CapeManager manager, int mouseX, int mouseY) {
        List<CapeEntry> capes = manager.getCapes();

        if (capes.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.translatable("text.capeswitcher.no_capes"),
                    rightPanelX + rightPanelW / 2,
                    rightPanelY + rightPanelH / 2 - 4,
                    COLOR_TEXT_DIM);
            return;
        }

        context.enableScissor(rightPanelX, cardListY, rightPanelX + rightPanelW, cardListY + cardListH);

        hoveredCardIndex = -1;
        String selectedCape = manager.getConfig().getSelectedCape();
        boolean isLocalMode = "local".equals(manager.getConfig().getCapeMode());

        for (int i = 0; i < capes.size(); i++) {
            CapeEntry cape = capes.get(i);
            int cardY = cardListY + i * (CARD_HEIGHT + CARD_GAP) - scrollOffset;

            if (cardY + CARD_HEIGHT < cardListY || cardY > cardListY + cardListH) continue;

            int cardX = rightPanelX + 4;
            int cardW = rightPanelW - 8;
            if (maxScroll > 0) {
                cardW -= 8;
            }

            boolean isSelected = isLocalMode && cape.getFileName().equals(selectedCape);
            boolean isHovered = mouseX >= cardX && mouseX <= cardX + cardW
                    && mouseY >= cardY && mouseY <= cardY + CARD_HEIGHT
                    && mouseY >= cardListY && mouseY <= cardListY + cardListH;

            if (isHovered) {
                hoveredCardIndex = i;
            }

            int bgColor;
            if (isSelected && isHovered) {
                bgColor = COLOR_CARD_SELECTED_HOVER;
            } else if (isSelected) {
                bgColor = COLOR_CARD_SELECTED;
            } else if (isHovered) {
                bgColor = COLOR_CARD_HOVER;
            } else {
                bgColor = COLOR_CARD_BG;
            }

            int borderColor = isSelected ? COLOR_CARD_SELECTED_BORDER : COLOR_CARD_BORDER;
            context.fill(cardX, cardY, cardX + cardW, cardY + CARD_HEIGHT, borderColor);
            context.fill(cardX + 1, cardY + 1, cardX + cardW - 1, cardY + CARD_HEIGHT - 1, bgColor);

            // Thumbnail
            int thumbX = cardX + CARD_PADDING;
            int thumbY = cardY + (CARD_HEIGHT - THUMB_SIZE) / 2;
            Identifier texId = cape.getTextureId();
            if (texId != null) {
                context.drawTexture(RenderLayer::getGuiTextured, texId,
                        thumbX, thumbY, 0.0f, 0.0f,
                        THUMB_SIZE, THUMB_SIZE, THUMB_SIZE, THUMB_SIZE);
            } else {
                context.fill(thumbX, thumbY, thumbX + THUMB_SIZE, thumbY + THUMB_SIZE, COLOR_PLACEHOLDER);
            }

            // Cape name
            int nameX = thumbX + THUMB_SIZE + CARD_PADDING + 2;
            int nameY = cardY + (CARD_HEIGHT - 8) / 2;
            int textColor = isSelected ? 0x55FF55 : COLOR_TEXT;
            context.drawTextWithShadow(this.textRenderer,
                    Text.literal(cape.getDisplayName()), nameX, nameY, textColor);

            if (isSelected) {
                context.drawTextWithShadow(this.textRenderer,
                        Text.literal("✔"),
                        cardX + cardW - 14, nameY, 0x55FF55);
            }
        }

        // Scrollbar
        if (maxScroll > 0) {
            int scrollBarX = rightPanelX + rightPanelW - 6;
            int scrollBarH = cardListH;
            int thumbHeight = Math.max(15, (int) ((float) cardListH / (cardListH + maxScroll) * scrollBarH));
            int thumbPos = cardListY + (int) ((float) scrollOffset / maxScroll * (scrollBarH - thumbHeight));

            context.fill(scrollBarX, cardListY, scrollBarX + 4, cardListY + scrollBarH, 0x40FFFFFF);
            context.fill(scrollBarX, thumbPos, scrollBarX + 4, thumbPos + thumbHeight, 0xAAFFFFFF);
        }

        context.disableScissor();
    }

    // --- Mouse interaction ---

    private boolean isMouseOverPreview(double mouseX, double mouseY) {
        return mouseX >= leftPanelX && mouseX <= leftPanelX + leftPanelW
                && mouseY >= leftPanelY && mouseY <= leftPanelY + leftPanelH;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Let buttons (Reset View, etc.) handle the click first
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // Left click on preview panel = start dragging
        if (button == 0 && isMouseOverPreview(mouseX, mouseY)) {
            draggingPreview = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }

        // Left click on cape card = select cape
        if (button == 0 && hoveredCardIndex >= 0) {
            CapeManager manager = CapeManager.getInstance();
            if (manager != null) {
                List<CapeEntry> capes = manager.getCapes();
                if (hoveredCardIndex < capes.size()) {
                    CapeEntry cape = capes.get(hoveredCardIndex);
                    manager.selectCape(cape);
                    rebuildScreen();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && draggingPreview) {
            double dx = mouseX - lastMouseX;
            double dy = mouseY - lastMouseY;

            previewYaw += (float) dx * ROTATION_SENSITIVITY;
            previewPitch += (float) dy * PITCH_SENSITIVITY;

            // Clamp pitch
            previewPitch = Math.max(-MAX_PITCH, Math.min(MAX_PITCH, previewPitch));

            // Normalize yaw to 0-360
            previewYaw = previewYaw % 360.0f;
            if (previewYaw < 0) previewYaw += 360.0f;

            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && draggingPreview) {
            draggingPreview = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        // Scroll on preview panel = zoom
        if (isMouseOverPreview(mouseX, mouseY)) {
            previewZoom += (float) verticalAmount * ZOOM_SENSITIVITY;
            previewZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, previewZoom));
            return true;
        }

        // Scroll on cape list = scroll list
        if (mouseX >= rightPanelX && mouseX <= rightPanelX + rightPanelW
                && mouseY >= rightPanelY && mouseY <= rightPanelY + rightPanelH) {
            scrollOffset -= (int) (verticalAmount * (CARD_HEIGHT + CARD_GAP) * 2);
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);

        CapeManager manager = CapeManager.getInstance();
        if (manager == null) return;

        // Update preview cape on hover over cape cards
        if (hoveredCardIndex >= 0 && hoveredCardIndex < manager.getCapes().size()) {
            manager.setPreviewCape(manager.getCapes().get(hoveredCardIndex));
        } else {
            manager.clearPreviewCape();
        }
    }

    @Override
    public void close() {
        CapeManager manager = CapeManager.getInstance();
        if (manager != null) {
            manager.clearPreviewCape();
        }
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void rebuildScreen() {
        this.clearChildren();
        this.init();
    }
}
