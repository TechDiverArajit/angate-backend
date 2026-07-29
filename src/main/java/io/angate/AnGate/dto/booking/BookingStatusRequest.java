package io.angate.AnGate.dto.booking;

import io.angate.AnGate.entity.Booking;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingStatusRequest {
    @NotNull(message = "Status is Required")
    private Booking.Status status;
}
