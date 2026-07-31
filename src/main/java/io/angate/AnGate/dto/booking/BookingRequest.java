package io.angate.AnGate.dto.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {

    @NotNull(message = "ticketType is required")
    private Long ticketTypeId;
    @Min(value = 1,message = "min 1 quantity required")
    private Integer quantity;

}
