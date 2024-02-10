package net.kremianskii.zettlekasten.domain;

import net.kremianskii.zettlekasten.api.CategoryId;
import net.kremianskii.zettlekasten.api.CategoryName;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.hash;
import static net.kremianskii.common.Checks.checkNonNull;

public final class Category {
    public final CategoryId id;
    public final CategoryName name;
    private final @Nullable CategoryId parentId;

    public Category(CategoryId id,
                    CategoryName name,
                    @Nullable CategoryId parentId) {
        this.id = checkNonNull(id, "id");
        this.name = checkNonNull(name, "name");
        this.parentId = parentId;
    }

    public Optional<CategoryId> parentId() {
        return Optional.ofNullable(parentId);
    }

    @Override
    public String toString() {
        return "Category{" +
            "id=" + id +
            ", name=" + name +
            ", parentId=" + parentId +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var category = (Category) o;
        return Objects.equals(id, category.id) &&
            Objects.equals(name, category.name) &&
            Objects.equals(parentId, category.parentId);
    }

    @Override
    public int hashCode() {
        return hash(id, name, parentId);
    }
}
