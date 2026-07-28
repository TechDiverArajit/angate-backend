package io.angate.AnGate.service;

import io.angate.AnGate.dto.booking.BookingRequest;
import io.angate.AnGate.dto.booking.BookingResponse;
import io.angate.AnGate.entity.Booking;
import io.angate.AnGate.entity.Event;
import io.angate.AnGate.entity.TicketType;
import io.angate.AnGate.entity.Users;
import io.angate.AnGate.exception.ResourceNotFoundException;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final ModelMapper modelMapper;
    private final EventRepository eventRepository;


    @Transactional
    public BookingResponse bookAnEvent(BookingRequest bookingRequest) throws InterruptedException {

            Users users = userRepository.findById(bookingRequest.getUserId())
                    .orElseThrow(()-> new IllegalArgumentException("Invalid user id"));
            TicketType ticketType = ticketTypeRepository.findById(bookingRequest.getTicketTypeId())
                    .orElseThrow(()-> new IllegalArgumentException("Ticket id doesn't exists"));
        Thread.sleep(3000);
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

            ticketTypeRepository.save(ticketType);
            Booking booking = bookingRepository.save(bookingToSaved);
            BookingResponse bookingResponse = modelMapper.map(booking,BookingResponse.class);
            bookingResponse.setEventTitle(ticketType.getEvent().getTitle());
            bookingResponse.setUserId(booking.getUsers().getId());
            bookingResponse.setTicketTypeId(booking.getTicketType().getId());
            return bookingResponse;

    }


    public BookingResponse getBookingById(Long booking_id) {
        Booking booking = bookingRepository.findById(booking_id).orElseThrow(() -> new ResourceNotFoundException("No bookings found with id: "+ booking_id));
        BookingResponse bookingResponse = modelMapper.map(booking,BookingResponse.class);
        bookingResponse.setUserId(booking.getUsers().getId());
        bookingResponse.setTicketTypeId(booking.getTicketType().getId());
        bookingResponse.setEventTitle(booking.getTicketType().getEvent().getTitle());
        return bookingResponse;

    }

    public List<BookingResponse> findBookingsByUserId(Long user_id){
        Users users = userRepository.findById(user_id).orElseThrow(() -> new ResourceNotFoundException("No user found with id: "+ user_id));
        List<Booking> bookings = bookingRepository.findByUsersId(users.getId());
        List<BookingResponse> bookingResponses = bookings.stream()
                .map(booking -> {
                    BookingResponse bookingResponse = modelMapper.map(booking,BookingResponse.class);
                    bookingResponse.setUserId(booking.getUsers().getId());
                    bookingResponse.setTicketTypeId(booking.getTicketType().getId());
                    bookingResponse.setEventTitle(booking.getTicketType().getEvent().getTitle());
                    return bookingResponse;
                })
                .collect(Collectors.toList());

        return bookingResponses;
    }
}
