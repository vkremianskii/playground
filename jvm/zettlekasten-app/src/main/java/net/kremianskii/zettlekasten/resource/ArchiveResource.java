package net.kremianskii.zettlekasten.resource;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import net.kremianskii.common.rest.Resource;
import net.kremianskii.zettlekasten.api.Archive;
import net.kremianskii.zettlekasten.api.Category;
import net.kremianskii.zettlekasten.api.Note;
import net.kremianskii.zettlekasten.domain.ArchiveRepository;

import java.io.IOException;

import static io.javalin.apibuilder.ApiBuilder.get;
import static net.kremianskii.common.Checks.checkNonNull;

public final class ArchiveResource extends Resource {
    private final ArchiveRepository repository;

    public ArchiveResource(final ArchiveRepository repository) {
        this.repository = checkNonNull(repository, "repository");
    }

    @Override
    public void registerRoutes() {
        get("/archive", this::getArchive);
    }

    private void getArchive(Context context) throws IOException {
        repository.find()
            .map(this::archiveFromDomainArchive)
            .ifPresentOrElse(
                context::json,
                () -> {
                    throw new NotFoundResponse();
                });
    }

    private Archive archiveFromDomainArchive(net.kremianskii.zettlekasten.domain.Archive archive) {
        return new Archive(
            archive.notes.stream().map(this::noteFromDomainNote).toList(),
            archive.categories.stream().map(this::categoryFromDomainCategory).toList());
    }

    private Note noteFromDomainNote(net.kremianskii.zettlekasten.domain.Note note) {
        return new Note(
            note.id,
            note.name(),
            note.text(),
            note.tags,
            note.categoryId().orElse(null));
    }

    private Category categoryFromDomainCategory(net.kremianskii.zettlekasten.domain.Category category) {
        return new Category(
            category.id,
            category.name,
            category.parentId().orElse(null));
    }
}
