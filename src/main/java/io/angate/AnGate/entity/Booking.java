package io.angate.AnGate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Booking extends BaseEntity{

    @JoinColumn(name = "user_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Users users;

    @JoinColumn(name = "ticket_type_id" , nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TicketType ticketType;

    private Integer quantity;
    private BigDecimal totalPrice;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private LocalDateTime expiryTime;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

    @Column(unique = true)
    private String bookingReference;

    @Column(unique = true)
    private String ticketCode;

    @Column(nullable = false)
    private Boolean checkedIn = false;

    private LocalDateTime checkedAt;

    public enum Status{
        PENDING,
        CONFIRMED,

        EXPIRED,
        REFUNDED
    }

    public enum PaymentStatus {

        PENDING,

        SUCCESS,

        FAILED,

        REFUNDED
    }

}
