package io.angate.AnGate.dto.booking;

import io.angate.AnGate.entity.Booking;
import lombok.Data;

import java.math.BigDecimal;

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
    private String razorpayOrderId;
    private Long amount;
    private String currency;
    private String key;;
}
