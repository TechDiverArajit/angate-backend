package io.angate.AnGate.service;

import io.angate.AnGate.dto.booking.BookingRequest;
import io.angate.AnGate.dto.booking.BookingResponse;
import io.angate.AnGate.dto.booking.BookingStatusRequest;
import io.angate.AnGate.entity.Booking;

import io.angate.AnGate.entity.TicketType;
import io.angate.AnGate.entity.Users;
import io.angate.AnGate.exception.BookingExistsDeletionException;
import io.angate.AnGate.exception.ResourceNotFoundException;
import io.angate.AnGate.repository.BookingRepository;

import io.angate.AnGate.repository.TicketTypeRepository;
import io.angate.AnGate.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

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


    @Transactional
    public BookingResponse bookAnEvent(BookingRequest bookingRequest , Users users) throws InterruptedException {

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
            Booking booking = bookingRepository.save(bookingToSaved);
            BookingResponse bookingResponse = modelMapper.map(booking,BookingResponse.class);
            bookingResponse.setEventTitle(ticketType.getEvent().getTitle());
            bookingResponse.setUserId(users.getId());
            bookingResponse.setTicketTypeId(ticketType.getId());
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

    public List<BookingResponse> findMyBookings(Users users){

        List<Booking> bookings = bookingRepository.findByUsersId(users.getId());

        return bookings.stream()
                .map(booking -> {
                    BookingResponse bookingResponse = modelMapper.map(booking,BookingResponse.class);
                    bookingResponse.setUserId(booking.getUsers().getId());
                    bookingResponse.setTicketTypeId(booking.getTicketType().getId());
                    bookingResponse.setEventTitle(booking.getTicketType().getEvent().getTitle());
                    return bookingResponse;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse updateBookingStatus(Long b_id, BookingStatusRequest request) {
        Booking booking = bookingRepository.findById(b_id)
                .orElseThrow(() -> new ResourceNotFoundException("No bookings found"));
        if(booking.getStatus()== Booking.Status.REFUNDED){
            throw new BookingExistsDeletionException("Refunded,Cannot be Updated");
        }

        Booking.Status oldStatus = booking.getStatus();
        Booking.Status newStatus = request.getStatus();
        booking.setStatus(newStatus);

        if(oldStatus!= Booking.Status.REFUNDED && newStatus== Booking.Status.REFUNDED){
            TicketType ticketType = booking.getTicketType();
            ticketType.setAvailableTickets(ticketType.getAvailableTickets()+booking.getQuantity());
            ticketTypeRepository.save(ticketType);

        }
        Booking booking1 = bookingRepository.save(booking);

        BookingResponse bookingResponse = modelMapper.map(booking1,BookingResponse.class);
        bookingResponse.setUserId(booking1.getUsers().getId());
        bookingResponse.setTicketTypeId(booking1.getTicketType().getId());
        bookingResponse.setEventTitle(booking1.getTicketType().getEvent().getTitle());
        return bookingResponse;
    }

    public List<BookingResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(booking -> {
                    BookingResponse bookingResponse = modelMapper.map(booking,BookingResponse.class);
                    bookingResponse.setUserId(booking.getUsers().getId());
                    bookingResponse.setEventTitle(booking.getTicketType().getEvent().getTitle());
                    bookingResponse.setTicketTypeId(booking.getTicketType().getId());
                    return bookingResponse;
                })
                .collect(Collectors.toList());
    }
}
