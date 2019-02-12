package de.godtitan.lobby.util;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileBuilder {

    @Getter
    private File file;

    @Getter
    private YamlConfiguration config;

    public FileBuilder(String path, String name) {
        this.file = new File(path, name);
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public FileBuilder setValue(String path, Object value) {
        config.set(path, value);
        return this;
    }

    public FileBuilder save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }
}
