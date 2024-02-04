package net.kremianskii.zettlekasten.api;

import java.util.regex.Pattern;

import static net.kremianskii.common.Checks.checkThat;

public record NoteName(String value) {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\d\\s]+$");

    public NoteName {
        checkThat(
            value,
            v -> v != null && NAME_PATTERN.matcher(v).matches(),
            "value must be non empty and contain only letters, digits and whitespace");
    }
}
