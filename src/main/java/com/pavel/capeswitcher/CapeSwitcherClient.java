package com.pavel.capeswitcher;

import com.pavel.capeswitcher.cape.CapeManager;
import com.pavel.capeswitcher.config.CapeConfig;
import com.pavel.capeswitcher.gui.CapeWardrobeScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapeSwitcherClient implements ClientModInitializer {
    public static final String MOD_ID = "capeswitcher";
    private static final Logger LOGGER = LoggerFactory.getLogger("CapeSwitcher");

    private static KeyBinding openWardrobeKey;
    private boolean capesLoaded = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Cape Switcher initializing...");

        CapeConfig config = CapeConfig.load();

        // Create manager but do NOT load textures yet — OpenGL context isn't ready
        CapeManager manager = new CapeManager(config);

        openWardrobeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.capeswitcher.open_wardrobe",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.capeswitcher"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Defer cape loading to first tick when OpenGL is ready
            if (!capesLoaded) {
                capesLoaded = true;
                manager.initialize();
                LOGGER.info("Cape Switcher capes loaded (deferred)");
            }

            while (openWardrobeKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new CapeWardrobeScreen());
                }
            }
        });

        LOGGER.info("Cape Switcher initialized successfully");
    }
}
