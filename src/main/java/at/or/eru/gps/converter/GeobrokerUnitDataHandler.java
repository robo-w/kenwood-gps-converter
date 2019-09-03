package at.or.eru.gps.converter;

import at.or.eru.gps.converter.configuration.GeobrokerConfiguration;
import at.or.eru.gps.converter.configuration.IgnoreTimestamp;
import at.or.eru.gps.converter.configuration.UnitConfiguration;
import at.or.eru.gps.converter.data.UnitId;
import at.or.eru.gps.converter.data.UnitPositionData;
import at.wrk.fmd.geobroker.contract.generic.Position;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wien.dragon.geobroker.lib.GeobrokerPositionSender;

import javax.inject.Inject;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.google.common.collect.Multimaps.toMultimap;

class GeobrokerUnitDataHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GeobrokerUnitDataHandler.class);
    private final GeobrokerPositionSender positionSender;
    private final Multimap<UnitId, UnitConfiguration> configuredUnits;
    private final boolean ignoreTimestamp;

    @Inject
    public GeobrokerUnitDataHandler(
            final GeobrokerPositionSender positionSender,
            final GeobrokerConfiguration configuration,
            @IgnoreTimestamp final boolean ignoreTimestamp) {
        this.positionSender = positionSender;
        configuredUnits = configuration
                .getUnitConfigurationList()
                .stream()
                .collect(toMultimap(x -> UnitId.fromString(x.getRadioUnitId()), Function.identity(), MultimapBuilder.hashKeys().hashSetValues()::build));
        this.ignoreTimestamp = ignoreTimestamp;
    }

    void handle(final UnitPositionData positionData) {
        LOG.debug("Handling position data: {}", positionData);

        Optional<UnitId> unitId = positionData.getUnitId();
        if (unitId.isPresent()) {
            Collection<UnitConfiguration> unitConfiguration = configuredUnits.get(unitId.get());

            if (unitConfiguration.isEmpty()) {
                logNoTargetConfigured(unitId.get());
            }

            Position updatedPosition = createPosition(positionData);
            unitConfiguration.forEach(unit -> sendPositionUpdate(updatedPosition, unit));

        } else {
            logNoUnitIdAttached(positionData);
        }

    }

    private void sendPositionUpdate(final Position updatedPosition, final UnitConfiguration unit) {
        LOG.debug("Sending position update of unit '{}' to Geobroker unit '{}'.", unit.getRadioUnitId(), unit.getGeobrokerUnitId());
        CompletableFuture<Position> future = positionSender.sendPositionUpdate(
                unit.getGeobrokerUnitId(),
                unit.getGeobrokerAuthenticationToken(),
                updatedPosition);
        future.whenComplete((position, throwable) -> {
            if (throwable != null) {
                LOG.warn("Sending position update failed: {}", throwable.getMessage());
            }
        });
    }

    private Position createPosition(final UnitPositionData positionData) {
        Instant timestamp = ignoreTimestamp
                ? Instant.now()
                : positionData.getTimestamp().toInstant(ZoneOffset.UTC);
        return new Position(positionData.getLatitude(), positionData.getLongitude(), timestamp, null, null, null);
    }

    private void logNoTargetConfigured(final UnitId unitId) {
        LOG.warn("Received GPS update without a configured unit for unitId '{}'", unitId);
    }

    private void logNoUnitIdAttached(final UnitPositionData positionData) {
        LOG.warn("Received GPS update without a valid unit ID: {}", positionData);
    }
}
