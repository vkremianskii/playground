package net.kremianskii.zettlekasten.api;

import net.kremianskii.common.Microtype;

import java.util.regex.Pattern;

import static net.kremianskii.common.Checks.checkThat;

public final class Tag extends Microtype<String> {
    private static final Pattern VALUE_PATTERN = Pattern.compile("^\\w+$");

    public Tag(final String value) {
        super(value);
        checkThat(
            value,
            v -> v != null && VALUE_PATTERN.matcher(v).matches(),
            "value must be non empty and contain only letters, digits and underscores");
    }
}
