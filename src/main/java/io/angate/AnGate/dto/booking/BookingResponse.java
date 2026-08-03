package io.angate.AnGate.dto.booking;

import io.angate.AnGate.entity.Booking;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long id;
    private Long userId;
    private String eventTitle;
    private Long ticketTypeId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Booking.Status status;
    private Booking.PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private String imageUrl;
    private String razorpayOrderId;
    private String bookingReference;
    private String emailId;
    private Long amount;
    private String currency;
    private String key;;
}
