package io.angate.AnGate.repository;

import io.angate.AnGate.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event , Long> {
}
