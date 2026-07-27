package io.angate.AnGate.dto.event;

import io.angate.AnGate.dto.tickettype.TicketTypeRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EventRequest {

    @NotBlank(message = "Event title is required")
    private String title;

    @NotBlank(message = "Event description is needed")
    private String description;

    @NotBlank(message = "Event venue is required")
    private String venue;

    @NotNull(message = "Date is required")
    private LocalDateTime startTime;


    private List<TicketTypeRequest> ticketTypes = new ArrayList<>();


}
