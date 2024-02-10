package net.kremianskii.zettlekasten.data;

import net.kremianskii.zettlekasten.domain.Archive;
import net.kremianskii.zettlekasten.domain.ArchiveRepository;

import java.util.Optional;

public final class MemoryArchiveRepository implements ArchiveRepository {
    private volatile Archive archive;

    @Override
    public void save(Archive archive) {
        this.archive = archive;
    }

    @Override
    public Optional<Archive> find() {
        return Optional.ofNullable(archive);
    }
}
