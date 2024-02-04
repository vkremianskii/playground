package net.kremianskii.zettlekasten.domain;

import net.kremianskii.zettlekasten.api.NoteName;
import net.kremianskii.zettlekasten.api.Tag;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.hash;
import static net.kremianskii.common.Checks.checkNonNull;

public final class Note {
    private NoteName name;
    private String text = "";
    public final Set<Tag> tags = new HashSet<>();

    public Note(final NoteName name) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var note = (Note) o;
        return Objects.equals(name, note.name) &&
            Objects.equals(text, note.text) &&
            Objects.equals(tags, note.tags);
    }

    @Override
    public int hashCode() {
        return hash(name, text, tags);
    }

    @Override
    public String toString() {
        return "Note{" +
            "name=" + name +
            ", text='" + text + '\'' +
            ", tags=" + tags +
            '}';
    }
}
