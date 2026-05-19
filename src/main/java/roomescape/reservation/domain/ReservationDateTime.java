package roomescape.reservation.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReservationDateTime(
        LocalDateTime value
) {
    public static ReservationDateTime of(LocalDate date, LocalTime time) {
        return new ReservationDateTime(LocalDateTime.of(date, time));
    }

    public LocalDate getDate() {
        return value.toLocalDate();
    }
}
