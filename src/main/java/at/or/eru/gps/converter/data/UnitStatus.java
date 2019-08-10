package at.or.eru.gps.converter.data;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public final class UnitStatus {
    private final String status;

    public static UnitStatus fromString(final String unitId) {
        UnitStatus instance;
        try {
            // Try to create status from Integer, to strip leading zeros and get consistent formatting of string.
            instance = fromInt(Integer.parseInt(unitId));
        } catch (NumberFormatException | NullPointerException ignored) {
            instance = new UnitStatus(unitId);
        }

        return instance;
    }

    public static UnitStatus fromInt(final int unitId) {
        Preconditions.checkArgument(unitId >= 0, "Unit status must not be negative.");
        return new UnitStatus(Integer.toString(unitId));
    }

    private UnitStatus(final String status) {
        this.status = Objects.requireNonNull(StringUtils.trimToNull(status), "Unit status must not be null or empty.");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitStatus unitId = (UnitStatus) o;
        return Objects.equals(status, unitId.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append(status)
                .toString();
    }
}
