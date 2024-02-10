package net.kremianskii.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

import static java.util.Objects.hash;
import static net.kremianskii.common.Checks.checkNonNull;

@JsonSerialize(using = MicrotypeSerializer.class)
public abstract class Microtype<T> {
    public final T value;

    protected Microtype(T value) {
        this.value = checkNonNull(value, "value");
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var microtype = (Microtype<?>) o;
        return Objects.equals(value, microtype.value);
    }

    @Override
    public int hashCode() {
        return hash(value);
    }
}
