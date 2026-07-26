package io.angate.AnGate.repository;

import io.angate.AnGate.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking , Long> {
}
