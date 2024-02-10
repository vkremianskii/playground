package net.kremianskii.zettlekasten.data;

import net.kremianskii.zettlekasten.api.NoteId;
import net.kremianskii.zettlekasten.api.NoteName;
import net.kremianskii.zettlekasten.api.Tag;
import net.kremianskii.zettlekasten.domain.Archive;
import net.kremianskii.zettlekasten.domain.ArchiveRepository;
import net.kremianskii.zettlekasten.domain.Note;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static net.kremianskii.common.Checks.checkNonNull;
import static net.kremianskii.zettlekasten.db.Tables.NOTE;
import static net.kremianskii.zettlekasten.db.Tables.NOTE_TAG;
import static org.jooq.SQLDialect.POSTGRES;

public final class DBArchiveRepository implements ArchiveRepository {
    private final DSLContext dsl;

    public DBArchiveRepository(final ConnectionProvider connectionProvider) {
        this.dsl = DSL.using(
            checkNonNull(connectionProvider, "connectionProvider"),
            POSTGRES);
    }

    @Override
    public void save(Archive archive) {
        dsl.transaction(tx -> {
            final var txDsl = tx.dsl();
            txDsl.deleteFrom(NOTE_TAG).execute();
            txDsl.deleteFrom(NOTE).execute();
            for (final var note : archive.notes) {
                txDsl.insertInto(NOTE)
                    .columns(NOTE.NOTE_ID, NOTE.NAME, NOTE.TEXT)
                    .values(note.id.value, note.name().value, note.text())
                    .execute();
                for (final var tag : note.tags) {
                    txDsl.insertInto(NOTE_TAG)
                        .columns(NOTE_TAG.NOTE_ID, NOTE_TAG.TEXT)
                        .values(note.id.value, tag.value)
                        .execute();
                }
            }
        });
    }

    @Override
    public Optional<Archive> find() {
        final var records = dsl.select(NOTE.NOTE_ID, NOTE.NAME, NOTE.TEXT, NOTE_TAG.TEXT)
            .from(NOTE).leftJoin(NOTE_TAG)
            .on(NOTE.NOTE_ID.eq(NOTE_TAG.NOTE_ID))
            .stream().toList();
        if (records.isEmpty()) {
            return empty();
        }
        final Map<NoteId, Note> noteIdToNote = new HashMap<>();
        for (final var record : records) {
            final var noteId = new NoteId(record.get(NOTE.NOTE_ID));
            final var note = noteIdToNote.computeIfAbsent(
                noteId,
                ignored -> new Note(
                    new NoteId(record.get(NOTE.NOTE_ID)),
                    new NoteName(record.get(NOTE.NAME))));
            note.setText(record.get(NOTE.TEXT));
            note.tag(new Tag(record.get(NOTE_TAG.TEXT)));
        }
        final var archive = new Archive();
        noteIdToNote.values().forEach(archive::add);
        return Optional.of(archive);
    }
}
