package net.kremianskii.zettlekasten.api;

import net.kremianskii.common.Microtype;

import java.util.UUID;

public final class CategoryId extends Microtype<UUID> {
    public CategoryId(String value) {
        super(UUID.fromString(value));
    }

    public CategoryId(UUID value) {
        super(value);
    }
}
