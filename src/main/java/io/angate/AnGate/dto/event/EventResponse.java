package io.angate.AnGate.dto.event;

import io.angate.AnGate.dto.tickettype.TicketTypeResponse;
import io.angate.AnGate.entity.Event;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private String venue;
    private LocalDateTime startTime;
    private Event.Status status;
    private String imageUrl;
    private List<TicketTypeResponse> ticketTypes = new ArrayList<>();


}
