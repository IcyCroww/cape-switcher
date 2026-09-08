package com.icycrow.capeswitcher.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CapeConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("CapeSwitcher");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private String capeMode = "account";
    private String selectedCape = "";
    private boolean randomOnJoin = false;

    private transient Path configPath;

    public CapeConfig() {
    }

    public static CapeConfig load() {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("cape-switcher");
        Path configFile = configDir.resolve("config.json");

        CapeConfig config;
        if (Files.exists(configFile)) {
            try {
                String json = Files.readString(configFile);
                config = GSON.fromJson(json, CapeConfig.class);
                if (config == null) {
                    LOGGER.warn("Config file was empty, using defaults");
                    config = new CapeConfig();
                }
                if (!isValidMode(config.capeMode)) {
                    LOGGER.warn("Invalid capeMode '{}', resetting to 'account'", config.capeMode);
                    config.capeMode = "account";
                }
            } catch (IOException | JsonSyntaxException e) {
                LOGGER.error("Failed to load config, using defaults", e);
                config = new CapeConfig();
            }
        } else {
            config = new CapeConfig();
        }

        config.configPath = configFile;
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(this));
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    private static boolean isValidMode(String mode) {
        return "account".equals(mode) || "local".equals(mode) || "none".equals(mode);
    }

    public String getCapeMode() {
        return capeMode;
    }

    public void setCapeMode(String capeMode) {
        if (isValidMode(capeMode)) {
            this.capeMode = capeMode;
        }
    }

    public String getSelectedCape() {
        return selectedCape;
    }

    public void setSelectedCape(String selectedCape) {
        this.selectedCape = selectedCape != null ? selectedCape : "";
    }

    public boolean isRandomOnJoin() {
        return randomOnJoin;
    }

    public void setRandomOnJoin(boolean randomOnJoin) {
        this.randomOnJoin = randomOnJoin;
    }
}

