package roomescape.reservation.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.ErrorCode;
import roomescape.global.exception.policy.PastReservationNotAllowedException;
import roomescape.global.exception.policy.ReservationConflictException;
import roomescape.global.exception.policy.ReservationUpdateNotAllowedException;
import roomescape.global.exception.validation.ThemeNotFoundException;
import roomescape.reservation.controller.dto.CreateReservationRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDateTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.dto.CreateReservationParams;
import roomescape.reservation.repository.dto.DuplicateReservationCondition;
import roomescape.reservation.repository.dto.UpdateReservationParams;
import roomescape.reservation.service.dto.RescheduleReservationInfo;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.ReservationTimeRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final Clock clock;

    public List<ReservationResponse> findAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> findAllReservationsByName(String name) {
        return reservationRepository.findByName(name).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public ReservationResponse reserve(CreateReservationRequest request) {
        validateReservationAvailable(request.date(), request.timeId(), request.themeId());

        CreateReservationParams params = new CreateReservationParams(request.name(), request.date(),
                request.timeId(), request.themeId());
        Reservation reservation = reservationRepository.save(params);

        return ReservationResponse.from(reservation);
    }

    @Transactional
    public ReservationResponse rescheduleReservation(RescheduleReservationInfo rescheduleReservationInfo) {
        Reservation reservation = reservationRepository.findById(rescheduleReservationInfo.id());
        validateReservationAvailable(rescheduleReservationInfo.date(), rescheduleReservationInfo.timeId(), reservation.getThemeId());

        ReservationTime reservedTime = reservationTimeRepository.findById(rescheduleReservationInfo.timeId());
        Reservation rescheduledReservation = reservation.reschedule(rescheduleReservationInfo.date(), reservedTime);
        reservationRepository.update(UpdateReservationParams.from(rescheduledReservation));

        return ReservationResponse.from(rescheduledReservation);
    }

    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id);
        validateFutureOrPresent(reservation);

        reservationRepository.deleteById(id);
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id);
        validateAlreadyCancelled(reservation);
        validateFutureOrPresent(reservation);

        reservationRepository.cancelById(id);
    }

    private void validateReservationAvailable(LocalDate reservationDate, Long timeId, Long themeId) {
        validateThemeExists(themeId);
        validateDateTimeNotPast(reservationDate, timeId);
        validateDuplicate(new DuplicateReservationCondition(reservationDate, timeId, themeId));
    }

    private void validateThemeExists(Long id) {
        if (themeRepository.existsById(id)) {
            return;
        }
        throw new ThemeNotFoundException();
    }

    private void validateDateTimeNotPast(LocalDate reservationDate, Long timeId) {
        LocalTime reservationTime = reservationTimeRepository.findById(timeId).getStartAt();

        ReservationDateTime reservationDateTime = ReservationDateTime.of(reservationDate, reservationTime);
        if(reservationDateTime.isCreatedBefore(LocalDateTime.now(clock))) {
            throw new PastReservationNotAllowedException();
        }
    }

    private void validateDuplicate(DuplicateReservationCondition reservationDate) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(
                reservationDate)) {
            throw new ReservationConflictException();
        }
    }

    private void validateFutureOrPresent(Reservation reservation) {
        if (reservation.isCreatedBefore(LocalDateTime.now(clock))) {
            throw new ReservationUpdateNotAllowedException();
        }
    }

    private void validateAlreadyCancelled(Reservation reservation) {
        if (reservation.isCancel()) {
            throw new ReservationUpdateNotAllowedException(ErrorCode.RESERVATION_ALREADY_CANCELLED);
        }
    }
}
