package net.kremianskii.zettlekasten.api;

import org.jetbrains.annotations.Nullable;

public record Category(CategoryName name,
                       @Nullable Category parent) {
}
