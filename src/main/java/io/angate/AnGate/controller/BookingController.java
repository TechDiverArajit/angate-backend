package io.angate.AnGate.controller;

import io.angate.AnGate.dto.booking.BookingRequest;
import io.angate.AnGate.dto.booking.BookingResponse;
import io.angate.AnGate.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/createBooking")
    public ResponseEntity<BookingResponse> bookAnEvent(@RequestBody BookingRequest bookingRequest){
        return new ResponseEntity<>(bookingService.bookAnEvent(bookingRequest), HttpStatus.CREATED);
    }
}
