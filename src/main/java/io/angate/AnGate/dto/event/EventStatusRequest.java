package io.angate.AnGate.dto.event;

import io.angate.AnGate.entity.Event;
import lombok.Data;

@Data
public class EventStatusRequest {

    private Event.Status status;
}
