/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2019 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter.data;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public final class UnitId {
    private final String id;

    public static UnitId fromString(final String unitId) {
        UnitId instance;
        try {
            // Try to create ID from Integer, to strip leading zeros and get consistent formatting of ID string.
            instance = fromInt(Integer.parseInt(unitId));
        } catch (NumberFormatException | NullPointerException ignored) {
            instance = new UnitId(unitId);
        }

        return instance;
    }

    public static UnitId fromInt(final int unitId) {
        Preconditions.checkArgument(unitId >= 0, "Unit ID must not be negative.");
        return new UnitId(Integer.toString(unitId));
    }

    private UnitId(final String id) {
        this.id = Objects.requireNonNull(StringUtils.trimToNull(id), "Unit ID must not be null or empty.");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitId unitId = (UnitId) o;
        return Objects.equals(id, unitId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}
