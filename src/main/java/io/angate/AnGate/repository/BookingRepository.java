package io.angate.AnGate.repository;

import io.angate.AnGate.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking , Long> {

    List<Booking> findByUsersId(Long id);
    boolean existsByTicketTypeEventId(Long eventId);
    boolean existsByTicketTypeId(Long eventId);

    Optional<Booking> findByTicketTypeId(Long id);

    Optional<Booking> findByRazorpayOrderId(String razorpayOrderId);
}
