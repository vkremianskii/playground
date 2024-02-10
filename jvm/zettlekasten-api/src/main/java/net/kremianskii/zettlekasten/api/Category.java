package net.kremianskii.zettlekasten.api;

import org.jetbrains.annotations.Nullable;

import static net.kremianskii.common.Checks.checkNonNull;

public record Category(CategoryId id,
                       CategoryName name,
                       @Nullable CategoryId parentId) {

    public Category {
        checkNonNull(id, "id");
        checkNonNull(name, "name");
    }
}
