package io.angate.AnGate.service;

import io.angate.AnGate.dto.booking.BookingRequest;
import io.angate.AnGate.dto.booking.BookingResponse;
import io.angate.AnGate.entity.Booking;
import io.angate.AnGate.entity.Event;
import io.angate.AnGate.entity.TicketType;
import io.angate.AnGate.entity.Users;
import io.angate.AnGate.repository.BookingRepository;
import io.angate.AnGate.repository.EventRepository;
import io.angate.AnGate.repository.TicketTypeRepository;
import io.angate.AnGate.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final ModelMapper modelMapper;
    private final EventRepository eventRepository;


    @Transactional
    public BookingResponse bookAnEvent(BookingRequest bookingRequest){

            Users users = userRepository.findById(bookingRequest.getUserId())
                    .orElseThrow(()-> new IllegalArgumentException("Invalid user id"));
            TicketType ticketType = ticketTypeRepository.findById(bookingRequest.getTicketTypeId())
                    .orElseThrow(()-> new IllegalArgumentException("Ticket id doesn't exists"));
            if(bookingRequest.getQuantity()>ticketType.getAvailableTickets()){
                throw new IllegalStateException("sorry only "+ticketType.getAvailableTickets()+" tickets are available");
            }
            ticketType.setAvailableTickets(ticketType.getAvailableTickets()-bookingRequest.getQuantity());


            BigDecimal totalPrice = ticketType.getPrice()
                    .multiply(BigDecimal.valueOf(bookingRequest.getQuantity()));

            Booking bookingToSaved = Booking.builder()
                    .users(users)
                    .ticketType(ticketType)
                    .quantity(bookingRequest.getQuantity())
                    .totalPrice(totalPrice)
                    .build();

            System.out.println(bookingToSaved.getUsers());
            System.out.println(bookingToSaved.getTicketType());

            Booking booking = bookingRepository.save(bookingToSaved);
            ticketTypeRepository.save(ticketType);
            BookingResponse bookingResponse = modelMapper.map(booking,BookingResponse.class);
            bookingResponse.setEventTitle(ticketType.getEvent().getTitle());
            bookingResponse.setUserId(booking.getUsers().getId());
            bookingResponse.setTicketTypeId(booking.getTicketType().getId());
            return bookingResponse;

    }



}
