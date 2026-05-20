package roomescape.time.domain;

import java.time.LocalTime;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import roomescape.global.exception.validation.InvalidIdException;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ReservationTime {

    @EqualsAndHashCode.Include
    private final Long id;
    private final LocalTime startAt;

    private ReservationTime(Long id, LocalTime startAt) {
        this.id = id;
        this.startAt = startAt;
    }

    public static ReservationTime create(LocalTime startAt) {
        return new ReservationTime(null, startAt);
    }

    public static ReservationTime createWithId(Long id, LocalTime startAt) {
        validateId(id);
        return new ReservationTime(id, startAt);
    }

    public ReservationTime withId(Long id) {
        validateId(id);
        return new ReservationTime(id, startAt);
    }

    private static void validateId(Long id) {
        if (Objects.isNull(id) || id <= 0) {
            throw new InvalidIdException(id);
        }
    }
}
