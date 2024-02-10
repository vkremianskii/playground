package net.kremianskii.zettlekasten.config;

import java.nio.file.Path;

public record Config(ServerConfig server,
                     ZettlekastenConfig zettlekasten) {
    public record ServerConfig(int port) {
    }

    public record ZettlekastenConfig(Path dir) {
    }
}
