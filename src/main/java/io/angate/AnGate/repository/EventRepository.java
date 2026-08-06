package io.angate.AnGate.repository;

import io.angate.AnGate.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event , Long> {

    List<Event> findByStatusNotIn(Collection<Event.Status> statuses);


    Page<Event> findByStatusNotIn(Collection<Event.Status> statuses , Pageable pageable);
    Page<Event> findByStatus(Event.Status status, Pageable pageable);


}
