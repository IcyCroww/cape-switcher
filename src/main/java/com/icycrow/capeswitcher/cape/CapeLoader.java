package com.icycrow.capeswitcher.cape;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CapeLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger("CapeSwitcher");

    private final Path capesDir;

    public CapeLoader(Path capesDir) {
        this.capesDir = capesDir;
    }

    public List<CapeEntry> loadCapes() {
        if (!Files.exists(capesDir)) {
            try {
                Files.createDirectories(capesDir);
                LOGGER.info("Created capes directory: {}", capesDir);
            } catch (IOException e) {
                LOGGER.error("Failed to create capes directory", e);
                return Collections.emptyList();
            }
        }

        List<CapeEntry> capes = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(capesDir, "*.png")) {
            for (Path file : stream) {
                try {
                    CapeEntry entry = loadSingleCape(file);
                    if (entry != null) {
                        capes.add(entry);
                        LOGGER.info("Loaded cape: {}", file.getFileName());
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to load cape file: {}", file.getFileName(), e);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read capes directory", e);
        }

        capes.sort((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
        return capes;
    }

    private CapeEntry loadSingleCape(Path file) {
        String fileName = file.getFileName().toString();
        String safeName = fileName.toLowerCase()
                .replaceAll("[^a-z0-9._-]", "_")
                .replaceAll("\\.png$", "");

        Identifier textureId = Identifier.of("capeswitcher", "capes/" + safeName);

        try (InputStream is = Files.newInputStream(file)) {
            NativeImage image = NativeImage.read(is);
            NativeImageBackedTexture texture = new NativeImageBackedTexture(
                    () -> "Cape Switcher cape " + fileName, image);
            MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, texture);
            return new CapeEntry(fileName, file, textureId);
        } catch (IOException e) {
            LOGGER.error("Failed to read image: {}", fileName, e);
            return null;
        }
    }

    public void unloadTextures(List<CapeEntry> capes) {
        for (CapeEntry cape : capes) {
            MinecraftClient.getInstance().getTextureManager().destroyTexture(cape.getTextureId());
        }
    }
}

