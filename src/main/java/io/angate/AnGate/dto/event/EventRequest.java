package io.angate.AnGate.dto.event;

import io.angate.AnGate.dto.tickettype.TicketTypeRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EventRequest {

    @NotBlank(message = "Event title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotBlank(message = "Event description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotBlank(message = "Event venue is required")
    @Size(max = 255, message = "Venue cannot exceed 255 characters")
    private String venue;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotEmpty(message = "At least one ticket type is required")
    @Valid
    private List<TicketTypeRequest> ticketTypes = new ArrayList<>();

}
