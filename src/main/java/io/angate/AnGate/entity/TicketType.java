package io.angate.AnGate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketType extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private Integer totalTickets;
    @Column(nullable = false)
    private Integer availableTickets;
    @JoinColumn(name = "event_id" , nullable = false)
    @ManyToOne( fetch = FetchType.LAZY)
    private Event event;

}
