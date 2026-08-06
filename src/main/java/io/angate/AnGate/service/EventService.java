package io.angate.AnGate.service;

import io.angate.AnGate.dto.event.EventRequest;
import io.angate.AnGate.dto.event.EventResponse;

import io.angate.AnGate.dto.event.EventUpdateRequest;
import io.angate.AnGate.entity.Event;
import io.angate.AnGate.entity.TicketType;
import io.angate.AnGate.entity.enums.TicketStatus;
import io.angate.AnGate.exception.BookingExistsDeletionException;
import io.angate.AnGate.exception.ResourceNotFoundException;
import io.angate.AnGate.repository.BookingRepository;
import io.angate.AnGate.repository.EventRepository;
import io.angate.AnGate.repository.TicketTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;

    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;

    public EventResponse createEvent(EventRequest eventRequest) {
        Event eventTobeSaved = modelMapper.map(eventRequest, Event.class);
        eventTobeSaved.setStatus(eventTobeSaved.fetchStatus());
        List<TicketType> ticketTypesList = eventTobeSaved.getTicketTypes();
        for (TicketType ticketType : ticketTypesList) {
            ticketType.setEvent(eventTobeSaved);
            ticketType.setAvailableTickets(ticketType.getTotalTickets());
            ticketType.setStatus(TicketStatus.ACTIVE);
        }
        Event event = eventRepository.save(eventTobeSaved);
        EventResponse eventResponse = modelMapper.map(event, EventResponse.class);
        return eventResponse;
    }

    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("No event found"));
        return modelMapper.map(event, EventResponse.class);
    }

    public Page<EventResponse> getAllEvent(int page, int size , Event.Status status) {
        Pageable pageable = PageRequest.of(page, size);
        if(status!=null){
            Page<Event> events = eventRepository.findByStatus(status , pageable);
            return events.map(event -> modelMapper.map(event,EventResponse.class));
        }
        Page<Event> events = eventRepository.findByStatusNotIn( List.of(Event.Status.DELETED , Event.Status.CANCELLED), pageable);
        return events.map(event -> modelMapper.map(event, EventResponse.class));

    }

    public Page<EventResponse> getAllEventForAdmins(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventRepository.findAll( pageable);
        return events.map(event -> modelMapper.map(event, EventResponse.class));
    }

    public EventResponse updateEvent(EventUpdateRequest eventRequest, Long event_id) throws BadRequestException {


        Event event = eventRepository.findById(event_id)
                .orElseThrow(()-> new ResourceNotFoundException("No event found"));

        if (event.getStatus() == Event.Status.COMPLETED ||
                event.getStatus() == Event.Status.DELETED) {
            throw new BadRequestException("Event is either completed or deleted");
        }

        if (eventRequest.getTitle() != null) {
            event.setTitle(eventRequest.getTitle());
        }

        if (eventRequest.getDescription() != null) {
            event.setDescription(eventRequest.getDescription());
        }

        if (eventRequest.getVenue() != null) {
            event.setVenue(eventRequest.getVenue());
        }

        if (eventRequest.getImageUrl() != null) {
            event.setImageUrl(eventRequest.getImageUrl());
        }

        if (eventRequest.getStartTime() != null) {
            event.setStartTime(eventRequest.getStartTime());
        }
        if(eventRequest.getStatus()!=null){
           if(eventRequest.getStatus()!= Event.Status.DELETED &&
                   eventRequest.getStatus()!= Event.Status.CANCELLED){
               throw new BadRequestException("\"Only CANCELLED and DELETED can be set manually.\"");
           }
            event.setStatus(eventRequest.getStatus());
        }
        eventRepository.save(event);
        return modelMapper.map(event,EventResponse.class);
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> events = eventRepository.findByStatusNotIn(
                List.of(Event.Status.COMPLETED, Event.Status.CANCELLED , Event.Status.DELETED)
        );
        for (Event event : events) {
            Event.Status newStatus = event.fetchStatus();
            if (event.getStatus() != newStatus) {
                event.setStatus(newStatus);
            }
        }

    }

    public HttpStatus deleteAnEvent(Long e_id) {
        Event event = eventRepository.findById(e_id)
                .orElseThrow(() -> new ResourceNotFoundException("No event found"));
        if (bookingRepository.existsByTicketTypeEventId(e_id)) {
            throw new BookingExistsDeletionException("Cannot delete Event with id: "+ event.getId() +" has bookings");
        }
        eventRepository.delete(event);
        return HttpStatus.OK;
    }
}
