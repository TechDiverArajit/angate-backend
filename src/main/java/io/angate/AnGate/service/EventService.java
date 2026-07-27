package io.angate.AnGate.service;

import io.angate.AnGate.dto.event.EventRequest;
import io.angate.AnGate.dto.event.EventResponse;
import io.angate.AnGate.dto.tickettype.TicketTypeRequest;
import io.angate.AnGate.dto.tickettype.TicketTypeResponse;
import io.angate.AnGate.entity.Event;
import io.angate.AnGate.entity.TicketType;
import io.angate.AnGate.repository.EventRepository;
import io.angate.AnGate.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
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
}
