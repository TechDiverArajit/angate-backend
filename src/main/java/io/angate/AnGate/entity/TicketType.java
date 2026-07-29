package io.angate.AnGate.entity;

import io.angate.AnGate.entity.enums.TicketStatus;
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

    @Version
    private Long version;

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

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.ACTIVE;

}
