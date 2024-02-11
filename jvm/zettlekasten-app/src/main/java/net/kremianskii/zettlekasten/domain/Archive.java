package net.kremianskii.zettlekasten.domain;

import net.kremianskii.zettlekasten.api.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.hash;

public final class Archive {
    public final List<Note> notes = new ArrayList<>();
    public final List<Category> categories = new ArrayList<>();

    public void add(final Note note) {
        notes.add(note);
    }

    public void add(final Category category) {
        categories.add(category);
    }

    public List<Note> notesByTag(Tag tag) {
        return notes.stream()
            .filter(note -> note.tagged(tag))
            .toList();
    }

    @Override
    public String toString() {
        return "Archive{" +
            "notes=" + notes +
            ", categories=" + categories +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var archive = (Archive) o;
        return Objects.equals(notes, archive.notes) &&
            Objects.equals(categories, archive.categories);
    }

    @Override
    public int hashCode() {
        return hash(notes, categories);
    }
}
