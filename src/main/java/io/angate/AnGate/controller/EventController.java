package io.angate.AnGate.controller;

import io.angate.AnGate.dto.event.EventRequest;
import io.angate.AnGate.dto.event.EventResponse;
import io.angate.AnGate.dto.tickettype.TicketTypeRequest;
import io.angate.AnGate.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long eventId){
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }

    @PostMapping("/createEvent")
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest eventRequest ){
        return new ResponseEntity<>(eventService.createEvent(eventRequest), HttpStatus.CREATED);
    }
}
