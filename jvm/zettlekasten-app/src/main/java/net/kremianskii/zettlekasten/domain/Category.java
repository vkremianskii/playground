package net.kremianskii.zettlekasten.domain;

import net.kremianskii.zettlekasten.api.CategoryName;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.hash;
import static net.kremianskii.common.Checks.checkNonNull;

public final class Category {
    public final CategoryName name;
    private final @Nullable Category parent;

    public Category(CategoryName name,
                    @Nullable Category parent) {
        this.name = checkNonNull(name, "name");
        this.parent = parent;
    }

    public Optional<Category> parent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var category = (Category) o;
        return Objects.equals(name, category.name) &&
            Objects.equals(parent, category.parent);
    }

    @Override
    public int hashCode() {
        return hash(name, parent);
    }
}
