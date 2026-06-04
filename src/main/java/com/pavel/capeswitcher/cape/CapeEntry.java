package com.pavel.capeswitcher.cape;

import net.minecraft.util.Identifier;

import java.nio.file.Path;

public class CapeEntry {
    private final String fileName;
    private final Path filePath;
    private final Identifier textureId;
    private final String displayName;

    public CapeEntry(String fileName, Path filePath, Identifier textureId) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.textureId = textureId;
        // Display name: remove .png extension
        String name = fileName;
        if (name.toLowerCase().endsWith(".png")) {
            name = name.substring(0, name.length() - 4);
        }
        this.displayName = name;
    }

    public String getFileName() {
        return fileName;
    }

    public Path getFilePath() {
        return filePath;
    }

    public Identifier getTextureId() {
        return textureId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
