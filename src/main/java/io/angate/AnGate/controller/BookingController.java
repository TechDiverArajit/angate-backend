package io.angate.AnGate.controller;

import io.angate.AnGate.dto.booking.BookingRequest;
import io.angate.AnGate.dto.booking.BookingResponse;
import io.angate.AnGate.dto.booking.BookingStatusRequest;
import io.angate.AnGate.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    @GetMapping("/{booking_id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long booking_id ){
        return ResponseEntity.ok(bookingService.getBookingById(booking_id));
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<BookingResponse>> findBookingsByUserId(@PathVariable Long user_id){
        return ResponseEntity.ok(bookingService.findBookingsByUserId(user_id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingResponse>> getAllBookings(){
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PostMapping("/createBooking")
    public ResponseEntity<BookingResponse> bookAnEvent(@RequestBody BookingRequest bookingRequest) throws InterruptedException {
        return new ResponseEntity<>(bookingService.bookAnEvent(bookingRequest), HttpStatus.CREATED);
    }

    @PatchMapping("/{b_id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(@PathVariable Long b_id,
                                                               @RequestBody @Valid BookingStatusRequest request){
        return ResponseEntity.ok(bookingService.updateBookingStatus(b_id,request));
    }
}
