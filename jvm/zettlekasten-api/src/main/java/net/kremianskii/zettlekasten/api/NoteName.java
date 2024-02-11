package net.kremianskii.zettlekasten.api;

import net.kremianskii.common.Microtype;

import java.util.regex.Pattern;

import static net.kremianskii.common.Checks.checkThat;

public final class NoteName extends Microtype<String> {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\w\\s]+$");

    public NoteName(final String value) {
        super(value);
        checkThat(
            value,
            v -> v != null && NAME_PATTERN.matcher(v).matches(),
            "value must be non empty and contain only word characters and whitespace");
    }
}
