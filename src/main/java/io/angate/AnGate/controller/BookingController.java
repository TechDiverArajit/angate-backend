package io.angate.AnGate.controller;

import io.angate.AnGate.dto.booking.BookingRequest;
import io.angate.AnGate.dto.booking.BookingResponse;
import io.angate.AnGate.dto.booking.BookingStatusRequest;
import io.angate.AnGate.entity.Users;
import io.angate.AnGate.service.BookingService;
import io.angate.AnGate.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final JwtService jwtService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{booking_id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long booking_id ){
        return ResponseEntity.ok(bookingService.getBookingById(booking_id));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<List<BookingResponse>> findMyBookings(@AuthenticationPrincipal Users users){
        return ResponseEntity.ok(bookingService.findMyBookings(users));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<BookingResponse>> getAllBookings(){
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/createBooking")
    public ResponseEntity<BookingResponse> bookAnEvent(@RequestBody BookingRequest bookingRequest ,
                                                       @AuthenticationPrincipal Users users) throws InterruptedException {
        return new ResponseEntity<>(bookingService.bookAnEvent(bookingRequest , users), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{b_id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(@PathVariable Long b_id,
                                                               @RequestBody @Valid BookingStatusRequest request){
        return ResponseEntity.ok(bookingService.updateBookingStatus(b_id,request));
    }
}
