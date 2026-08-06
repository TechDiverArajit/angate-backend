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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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


    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updatePendingBookings(){
                                                            //find bookings which has PENDING status and ExpiryTime < NOW;
        List<Booking> expiredBookings = bookingRepository.findByStatusAndExpiryTimeBefore(Booking.Status.PENDING , LocalDateTime.now());
        for(Booking booking:expiredBookings){
            TicketType ticketType = booking.getTicketType();
            ticketType.setAvailableTickets(ticketType.getAvailableTickets()+booking.getQuantity());
            booking.setPaymentStatus(Booking.PaymentStatus.FAILED);
            booking.setStatus(Booking.Status.EXPIRED);
            ticketTypeRepository.save(ticketType);
        }


        bookingRepository.saveAll(expiredBookings);
    }




    public BookingResponse getBookingById(Long booking_id) {
        Booking booking = bookingRepository.findById(booking_id).orElseThrow(() -> new ResourceNotFoundException("No bookings found with id: "+ booking_id));
        BookingResponse bookingResponse = modelMapper.map(booking,BookingResponse.class);
        bookingResponse.setUserId(booking.getUsers().getId());
        bookingResponse.setTicketTypeId(booking.getTicketType().getId());
        bookingResponse.setEventTitle(booking.getTicketType().getEvent().getTitle());
        bookingResponse.setEmailId(booking.getUsers().getEmailId());
        return bookingResponse;

    }

    public Page<BookingResponse> findMyBookings(int page , int size , Users users){
        Pageable pageable = PageRequest.of(page,size , Sort.by(Sort.Direction.DESC , "id"));

        Page<Booking> bookings = bookingRepository.findByUsersId(users.getId(),pageable);

        return bookings
                .map(booking -> {
                    BookingResponse bookingResponse = modelMapper.map(booking,BookingResponse.class);
                    bookingResponse.setUserId(booking.getUsers().getId());
                    bookingResponse.setTicketTypeId(booking.getTicketType().getId());
                    bookingResponse.setEventTitle(booking.getTicketType().getEvent().getTitle());
                    bookingResponse.setRazorpayOrderId(booking.getRazorpayOrderId());
                    bookingResponse.setPaymentStatus(booking.getPaymentStatus());
                    bookingResponse.setBookingReference(booking.getBookingReference());
                    bookingResponse.setEmailId(users.getEmailId());
                    bookingResponse.setImageUrl(booking.getTicketType().getEvent().getImageUrl());
                    bookingResponse.setTicketName(booking.getTicketType().getName());
                    return bookingResponse;
                });
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

    public Page<BookingResponse> getAllBookings(int page , int size) {
        Pageable pageable = PageRequest.of(page , size);
        Page<Booking> bookings = bookingRepository.findAll(pageable);
        return bookings.map(booking -> {
                    BookingResponse bookingResponse = modelMapper.map(booking,BookingResponse.class);
                    bookingResponse.setUserId(booking.getUsers().getId());
                    bookingResponse.setEventTitle(booking.getTicketType().getEvent().getTitle());
                    bookingResponse.setTicketTypeId(booking.getTicketType().getId());
                    bookingResponse.setEmailId(booking.getUsers().getEmailId());
                    return bookingResponse;
                });
    }


    public Page<BookingResponse> getBookingsByUserId(Long userId ,int page , int size) {
        Pageable pageable = PageRequest.of(page,size);
        Users user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("No user found"));
        Page<Booking> bookings = bookingRepository.findByUsersId(userId , pageable);
        return bookings.map(booking -> {
            BookingResponse bookingResponse = modelMapper.map(booking,BookingResponse.class);
            bookingResponse.setUserId(booking.getUsers().getId());
            bookingResponse.setEmailId(booking.getUsers().getEmailId());
            bookingResponse.setTicketTypeId(booking.getTicketType().getId());
            bookingResponse.setEventTitle(booking.getTicketType().getEvent().getTitle());
            return bookingResponse;
        });
    }

}
