package io.angate.AnGate.service;

import io.angate.AnGate.dto.event.EventRequest;
import io.angate.AnGate.dto.event.EventResponse;
import io.angate.AnGate.dto.tickettype.TicketTypeRequest;
import io.angate.AnGate.dto.tickettype.TicketTypeResponse;
import io.angate.AnGate.entity.Booking;
import io.angate.AnGate.entity.Event;
import io.angate.AnGate.entity.TicketType;
import io.angate.AnGate.exception.ResourceNotFoundException;
import io.angate.AnGate.repository.EventRepository;
import io.angate.AnGate.repository.TicketTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final ModelMapper modelMapper;
    public EventResponse createEvent(EventRequest eventRequest) {
        Event eventTobeSaved = modelMapper.map(eventRequest,Event.class);
        eventTobeSaved.setStatus(eventTobeSaved.fetchStatus());
        List<TicketType> ticketTypesList = eventTobeSaved.getTicketTypes();
        for(TicketType ticketType: ticketTypesList){
            ticketType.setEvent(eventTobeSaved);
            ticketType.setAvailableTickets(ticketType.getTotalTickets());
        }
        Event event = eventRepository.save(eventTobeSaved);
        EventResponse eventResponse = modelMapper.map(event,EventResponse.class);
        return eventResponse;
    }

    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        return modelMapper.map(event,EventResponse.class);
    }

    public Page<EventResponse> getAllEvent(int page , int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Event> events = eventRepository.findAll(pageable);
        return events.map(event -> modelMapper.map(event,EventResponse.class));

    }

    public EventResponse updateEvent(EventRequest eventRequest , Long event_id) {
        if(!eventRepository.existsById(event_id)){
            throw new ResourceNotFoundException("not found event id: "+event_id);
        }
        Event event = modelMapper.map(eventRequest,Event.class);
        event.setId(event_id);
        event.setStatus(event.fetchStatus());
        List<TicketType> ticketTypes = event.getTicketTypes();
        for(TicketType ticketType: ticketTypes){
            ticketType.setAvailableTickets(ticketType.getTotalTickets());
            ticketType.setEvent(event);
        }
        Event eventTobeSaved = eventRepository.save(event);
        return modelMapper.map(eventTobeSaved,EventResponse.class);


    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void  updateStatus(){
        System.out.println("Scheduler running at " + LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        List<Event> events = eventRepository.findByStatusNotIn(
                List.of(Event.Status.COMPLETED,Event.Status.CANCELLED)
        );
        System.out.println("Events found: " + events.size());


        for(Event event: events){
            Event.Status newStatus = event.fetchStatus() ;

            if(event.getStatus() != newStatus){
                event.setStatus(newStatus);
            }
        }

    }

}
