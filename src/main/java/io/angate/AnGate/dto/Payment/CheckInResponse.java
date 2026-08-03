package io.angate.AnGate.dto.Payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckInResponse {
    private boolean success;
    private String message;
    private String event;
    private String attendee;
    private String ticketType;
}
