package com.icycrow.capeswitcher.cape;

import com.icycrow.capeswitcher.config.CapeConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class CapeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("CapeSwitcher");
    private static CapeManager instance;

    private final CapeLoader loader;
    private final CapeConfig config;
    private final Path capesDir;
    private List<CapeEntry> capes = Collections.emptyList();
    private CapeEntry currentCape;
    private CapeEntry previewCape;

    public CapeManager(CapeConfig config) {
        this.config = config;
        this.capesDir = FabricLoader.getInstance().getConfigDir()
                .resolve("cape-switcher").resolve("capes");
        this.loader = new CapeLoader(capesDir);
        instance = this;
    }

    public static CapeManager getInstance() {
        return instance;
    }

    public void initialize() {
        reloadCapes();
    }

    public void reloadCapes() {
        loader.unloadTextures(capes);
        capes = loader.loadCapes();

        currentCape = null;
        if ("local".equals(config.getCapeMode()) && !config.getSelectedCape().isEmpty()) {
            for (CapeEntry cape : capes) {
                if (cape.getFileName().equals(config.getSelectedCape())) {
                    currentCape = cape;
                    break;
                }
            }
            if (currentCape == null) {
                LOGGER.warn("Selected cape '{}' not found, switching to account mode",
                        config.getSelectedCape());
                config.setCapeMode("account");
                config.setSelectedCape("");
                config.save();
            }
        }

        LOGGER.info("Loaded {} capes", capes.size());
    }

    public void selectCape(CapeEntry cape) {
        if (cape == null) {
            config.setCapeMode("account");
            config.setSelectedCape("");
        } else {
            config.setCapeMode("local");
            config.setSelectedCape(cape.getFileName());
        }
        currentCape = cape;
        config.save();
    }

    public void setModeAccount() {
        config.setCapeMode("account");
        currentCape = null;
        config.save();
    }

    public void setModeNone() {
        config.setCapeMode("none");
        currentCape = null;
        config.save();
    }

    public void disableCape() {
        setModeNone();
    }

    public void setPreviewCape(CapeEntry cape) {
        this.previewCape = cape;
    }

    public void clearPreviewCape() {
        this.previewCape = null;
    }

    public CapeEntry getPreviewCape() {
        return previewCape;
    }

    /**
     * Returns the cape texture that should be rendered right now.
     * Preview cape takes priority (for hover in GUI), then current selected cape.
     */
    public Identifier getEffectiveCapeTexture() {
        if (previewCape != null) {
            return previewCape.getTextureId();
        }
        if (currentCape != null) {
            return currentCape.getTextureId();
        }
        return null;
    }

    public Identifier getCurrentCapeTexture() {
        if (currentCape != null) {
            return currentCape.getTextureId();
        }
        return null;
    }

    public CapeEntry getCurrentCape() {
        return currentCape;
    }

    public List<CapeEntry> getCapes() {
        return Collections.unmodifiableList(capes);
    }

    public CapeConfig getConfig() {
        return config;
    }

    public Path getCapesDir() {
        return capesDir;
    }
}

