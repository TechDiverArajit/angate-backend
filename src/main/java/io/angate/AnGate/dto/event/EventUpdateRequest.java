package io.angate.AnGate.dto.event;

import io.angate.AnGate.entity.Event;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventUpdateRequest {

    private String title;

    private String description;

    private String venue;

    private LocalDateTime startTime;

    private String imageUrl;

    private Event.Status status;

}
