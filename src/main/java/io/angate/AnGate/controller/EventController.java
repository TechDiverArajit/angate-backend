package io.angate.AnGate.controller;
import io.angate.AnGate.dto.event.EventRequest;
import io.angate.AnGate.dto.event.EventResponse;
import io.angate.AnGate.dto.event.EventUpdateRequest;
import io.angate.AnGate.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long eventId){
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvent(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(eventService.getAllEvent(page,size));
    }



    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody @Valid EventRequest eventRequest ){
        return new ResponseEntity<>(eventService.createEvent(eventRequest), HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{event_id}")
    public ResponseEntity<EventResponse> updateEvent(@RequestBody @Valid EventUpdateRequest updateRequest,
                                                     @PathVariable Long event_id ) throws BadRequestException {
        System.out.println("update hit");
        return new ResponseEntity<>(eventService.updateEvent(updateRequest , event_id ),HttpStatus.ACCEPTED);
    }




    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{e_id}")
    public ResponseEntity<HttpStatus> deleteAnEvent(@PathVariable Long e_id){
        return ResponseEntity.ok(eventService.deleteAnEvent(e_id));

    }



}
