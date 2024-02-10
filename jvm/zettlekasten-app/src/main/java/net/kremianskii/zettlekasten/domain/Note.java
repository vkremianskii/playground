package net.kremianskii.zettlekasten.domain;

import net.kremianskii.zettlekasten.api.CategoryId;
import net.kremianskii.zettlekasten.api.NoteId;
import net.kremianskii.zettlekasten.api.NoteName;
import net.kremianskii.zettlekasten.api.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.hash;
import static net.kremianskii.common.Checks.checkNonNull;

public final class Note {
    public final NoteId id;
    private NoteName name;
    private String text = "";
    public final Set<Tag> tags = new HashSet<>();
    private @Nullable CategoryId categoryId = null;

    public Note(final NoteId id,
                final NoteName name) {
        this.id = checkNonNull(id, "id");
        rename(name);
    }

    public void rename(final NoteName name) {
        this.name = checkNonNull(name, "name");
    }

    public NoteName name() {
        return name;
    }

    public void setText(final String text) {
        this.text = checkNonNull(text, "text");
    }

    public String text() {
        return text;
    }

    public void tag(final Tag tag) {
        tags.add(tag);
    }

    public boolean tagged(final Tag tag) {
        return tags.contains(tag);
    }

    public void setCategoryId(@Nullable final CategoryId categoryId) {
        this.categoryId = categoryId;
    }

    public Optional<CategoryId> categoryId() {
        return Optional.ofNullable(categoryId);
    }

    @Override
    public String toString() {
        return "Note{" +
            "id=" + id +
            ", name=" + name +
            ", text='" + text + '\'' +
            ", tags=" + tags +
            ", categoryId=" + categoryId +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var note = (Note) o;
        return Objects.equals(id, note.id) &&
            Objects.equals(name, note.name) &&
            Objects.equals(text, note.text) &&
            Objects.equals(tags, note.tags) &&
            Objects.equals(categoryId, note.categoryId);
    }

    @Override
    public int hashCode() {
        return hash(id, name, text, tags, categoryId);
    }
}
