package io.angate.AnGate.repository;

import io.angate.AnGate.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking , Long> {

    Page<Booking> findByUsersId(Long id , Pageable pageable);
    boolean existsByTicketTypeEventId(Long eventId);
    boolean existsByTicketTypeId(Long eventId);

    Optional<Booking> findByTicketTypeId(Long id);

    Optional<Booking> findByRazorpayOrderId(String razorpayOrderId);
    List<Booking> findByStatusAndExpiryTimeBefore(Booking.Status status, LocalDateTime time);

    Optional<Booking> findByTicketCode(String ticketCode);
}
