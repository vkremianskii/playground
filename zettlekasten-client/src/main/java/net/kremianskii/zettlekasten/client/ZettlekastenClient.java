package net.kremianskii.zettlekasten.client;

import net.kremianskii.zettlekasten.api.Archive;

import java.util.Optional;

public interface ZettlekastenClient {
    Optional<Archive> findArchive();
}
