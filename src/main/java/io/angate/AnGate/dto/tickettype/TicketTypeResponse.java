package io.angate.AnGate.dto.tickettype;

import io.angate.AnGate.entity.Event;
import io.angate.AnGate.entity.enums.TicketStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TicketTypeResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer totalTickets;
    private Integer availableTickets;
    private TicketStatus status;
}
