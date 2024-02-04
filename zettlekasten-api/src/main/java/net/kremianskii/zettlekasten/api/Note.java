package net.kremianskii.zettlekasten.api;

import java.util.Set;

public record Note(NoteName name,
                   String text,
                   Set<Tag> tags) {
}
