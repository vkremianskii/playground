package net.kremianskii.zettlekasten.api;

import net.kremianskii.common.Microtype;

import static net.kremianskii.common.Checks.checkNonEmpty;

public final class CategoryName extends Microtype<String> {
    public CategoryName(String value) {
        super(checkNonEmpty(value, "value"));
    }
}
