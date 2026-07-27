package io.angate.AnGate.dto.tickettype;

import io.angate.AnGate.entity.Event;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class TicketTypeRequest {

    private String name;
    private BigDecimal price;
    private Integer totalTickets;
}
