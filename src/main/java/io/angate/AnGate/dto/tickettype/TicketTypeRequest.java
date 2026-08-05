package io.angate.AnGate.dto.tickettype;

import io.angate.AnGate.entity.Event;
import io.angate.AnGate.entity.enums.TicketStatus;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class TicketTypeRequest {
    @Pattern(
            regexp = ".*\\S.*",
            message = "Name cannot be blank"
    )
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    @Positive(message = "Total tickets must be greater than 0")
    private Integer totalTickets;

    private TicketStatus status;
}
