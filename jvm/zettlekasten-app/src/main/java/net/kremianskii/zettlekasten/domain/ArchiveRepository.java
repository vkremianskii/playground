package net.kremianskii.zettlekasten.domain;

import java.io.IOException;
import java.util.Optional;

public interface ArchiveRepository {

    void save(Archive archive) throws IOException;

    Optional<Archive> find() throws IOException;
}
