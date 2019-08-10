package at.or.eru.gps.converter.configuration;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class UnitConfiguration {
    private final String geobrokerUnitId;
    private final String geobrokerAuthenticationToken;
    private final String radioUnitId;

    public UnitConfiguration(
            final String geobrokerUnitId,
            final String geobrokerAuthenticationToken,
            final String radioUnitId) {
        this.geobrokerUnitId = Objects.requireNonNull(geobrokerUnitId);
        this.geobrokerAuthenticationToken = Objects.requireNonNull(geobrokerAuthenticationToken);
        this.radioUnitId = Objects.requireNonNull(radioUnitId);
    }

    public String getGeobrokerUnitId() {
        return geobrokerUnitId;
    }

    public String getGeobrokerAuthenticationToken() {
        return geobrokerAuthenticationToken;
    }

    public String getRadioUnitId() {
        return radioUnitId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitConfiguration that = (UnitConfiguration) o;
        return Objects.equals(geobrokerUnitId, that.geobrokerUnitId) &&
                Objects.equals(geobrokerAuthenticationToken, that.geobrokerAuthenticationToken) &&
                Objects.equals(radioUnitId, that.radioUnitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geobrokerUnitId, geobrokerAuthenticationToken, radioUnitId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("geobrokerUnitId", geobrokerUnitId)
                .append("geobrokerAuthenticationToken", geobrokerAuthenticationToken)
                .append("radioUnitId", radioUnitId)
                .toString();
    }
}
