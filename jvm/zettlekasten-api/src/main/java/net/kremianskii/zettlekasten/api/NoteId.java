package net.kremianskii.zettlekasten.api;

import net.kremianskii.common.Microtype;

import java.util.UUID;

public final class NoteId extends Microtype<UUID> {
    public NoteId(String value) {
        super(UUID.fromString(value));
    }

    public NoteId(UUID value) {
        super(value);
    }
}
