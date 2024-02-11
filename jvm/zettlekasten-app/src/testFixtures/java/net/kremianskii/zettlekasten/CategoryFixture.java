package net.kremianskii.zettlekasten;

import net.kremianskii.zettlekasten.api.CategoryId;
import net.kremianskii.zettlekasten.api.CategoryName;
import net.kremianskii.zettlekasten.domain.Category;
import org.jetbrains.annotations.Nullable;

import static java.util.UUID.randomUUID;

public final class CategoryFixture {
    private CategoryFixture() {
    }

    public static Category aCategory(final CategoryName name,
                                     @Nullable final CategoryId parentId) {
        return new Category(
            new CategoryId(randomUUID()),
            name,
            parentId);
    }
}
