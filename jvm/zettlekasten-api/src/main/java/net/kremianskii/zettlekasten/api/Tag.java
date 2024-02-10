package net.kremianskii.zettlekasten.api;

import java.util.regex.Pattern;

import static net.kremianskii.common.Checks.checkThat;

public record Tag(String value) {
    private static final Pattern VALUE_PATTERN = Pattern.compile("^\\w+$");

    public Tag {
        checkThat(
            value,
            v -> v != null && VALUE_PATTERN.matcher(v).matches(),
            "value must be non empty and contain only letters, digits and underscores");
    }
}
