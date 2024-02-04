package net.kremianskii.zettlekasten.resource;

import com.sun.net.httpserver.HttpExchange;
import net.kremianskii.common.rest.Resource;
import net.kremianskii.common.rest.Route;
import net.kremianskii.common.rest.http.HttpMethod;
import net.kremianskii.zettlekasten.api.Archive;
import net.kremianskii.zettlekasten.api.Note;
import net.kremianskii.zettlekasten.domain.ArchiveRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static net.kremianskii.common.Checks.checkNonNull;

public final class ArchiveResource extends Resource {
    private final ArchiveRepository repository;

    public ArchiveResource(final ArchiveRepository repository) {
        this.repository = checkNonNull(repository, "repository");
        registerRoutes(List.of(
            new Route(HttpMethod.GET, "/archive", this::getArchive)));
    }

    private Optional<Archive> getArchive(HttpExchange exchange) throws IOException {
        return repository.find().map(this::archiveFromDomainArchive);
    }

    private Archive archiveFromDomainArchive(net.kremianskii.zettlekasten.domain.Archive archive) {
        return new Archive(archive.notes.stream().map(this::noteFromDomainNote).toList());
    }

    private Note noteFromDomainNote(net.kremianskii.zettlekasten.domain.Note note) {
        return new Note(note.name(), note.text(), note.tags);
    }
}
