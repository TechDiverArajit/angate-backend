package io.angate.AnGate.controller;

import com.google.zxing.WriterException;
import com.razorpay.RazorpayException;
import io.angate.AnGate.dto.Payment.PaymentVerificationRequest;
import io.angate.AnGate.dto.booking.BookingRequest;
import io.angate.AnGate.dto.booking.BookingResponse;
import io.angate.AnGate.entity.Users;
import io.angate.AnGate.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")



    public ResponseEntity<BookingResponse> createOrder(@RequestBody @Valid BookingRequest request ,
                                                       @AuthenticationPrincipal Users users) throws BadRequestException, RazorpayException {
        System.out.println("Payment controller reached");
        BookingResponse bookingResponse = paymentService.createOrder(request,users);
        return ResponseEntity.ok(bookingResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody PaymentVerificationRequest request) throws BadRequestException, RazorpayException {
        paymentService.verifyPayment(request);
        return ResponseEntity.ok("Payment Verified");
    }

    @GetMapping(value = "/{bId}/qr",produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQR(@PathVariable Long bId) throws IOException, WriterException {
        byte[] qr = paymentService.generateBookingQr(bId);
        return ResponseEntity.ok(qr);
    }
}
