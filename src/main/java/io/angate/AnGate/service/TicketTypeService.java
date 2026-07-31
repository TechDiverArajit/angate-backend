package io.angate.AnGate.service;

import io.angate.AnGate.dto.booking.BookingResponse;
import io.angate.AnGate.dto.tickettype.TicketTypeRequest;
import io.angate.AnGate.dto.tickettype.TicketTypeResponse;
import io.angate.AnGate.entity.Event;
import io.angate.AnGate.entity.TicketType;
import io.angate.AnGate.exception.BookingExistsDeletionException;
import io.angate.AnGate.exception.ResourceNotFoundException;
import io.angate.AnGate.repository.BookingRepository;
import io.angate.AnGate.repository.EventRepository;
import io.angate.AnGate.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    private final BookingRepository bookingRepository;
    public List<TicketTypeResponse> getTicketsByEventId(long eventId) {
        List<TicketType> ticketTypes = ticketTypeRepository.findByEventId(eventId);
        return ticketTypes.stream()
                .map(ticketType -> modelMapper.map(ticketType, TicketTypeResponse.class))
                .collect(Collectors.toList());
    }


    public TicketTypeResponse getTicketById(Long id) {
        TicketType ticketType = ticketTypeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No ticket found"));
        return modelMapper.map(ticketType,TicketTypeResponse.class);
    }


    public TicketTypeResponse createTicketType(TicketTypeRequest ticketTypeRequest,Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("no event found"));
        TicketType ticketType = modelMapper.map(ticketTypeRequest,TicketType.class);
        ticketType.setEvent(event);
        ticketType.setAvailableTickets(ticketTypeRequest.getTotalTickets());
        TicketType ticketTypeSaved = ticketTypeRepository.save(ticketType);
        return modelMapper.map(ticketTypeSaved,TicketTypeResponse.class);
    }

    public void deleteTicketType(Long id) {
        TicketType ticketType = ticketTypeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No ticketType Found"));
        if(bookingRepository.existsByTicketTypeId(ticketType.getId())){
            throw new BookingExistsDeletionException("Cannot delete the ticket because booking exists with ticket id: "+id);
        }
        ticketTypeRepository.delete(ticketType);

    }

    public TicketTypeResponse updatePartialTicket(Long id, TicketTypeRequest request) {
        TicketType ticketType = ticketTypeRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("No ticket type found to update"));
        if(request.getTotalTickets()!=null){
            int soldTickets = ticketType.getTotalTickets() - ticketType.getAvailableTickets();
            if(request.getTotalTickets()<soldTickets){
                throw new BookingExistsDeletionException("TotalTickets cannot be less than already booked ones");
            }
            ticketType.setTotalTickets(request.getTotalTickets());
            ticketType.setAvailableTickets(request.getTotalTickets() - soldTickets);
        }
        if(request.getName()!=null){
            ticketType.setName(request.getName());
        }
        if(request.getPrice()!=null){
            ticketType.setPrice(request.getPrice());
        }
        TicketType ticketType1 = ticketTypeRepository.save(ticketType);
        return modelMapper.map(ticketType1,TicketTypeResponse.class);
    }
}
