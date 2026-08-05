package io.angate.AnGate.dto.tickettype;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTicketTypeRequest {
    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @NotNull
    @Positive
    @Min(value = 1 , message = "minimum 1 quantity is required")
    private Integer totalTickets;

}
