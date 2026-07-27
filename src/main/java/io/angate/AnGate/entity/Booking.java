package io.angate.AnGate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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
    private Status status = Status.CONFIRMED;

    public enum Status{
        PENDING,
        CONFIRMED,
        FAILED
    }

}
