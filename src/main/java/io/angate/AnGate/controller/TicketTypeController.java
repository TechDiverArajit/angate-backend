package io.angate.AnGate.controller;

import io.angate.AnGate.dto.tickettype.TicketTypeRequest;
import io.angate.AnGate.dto.tickettype.TicketTypeResponse;
import io.angate.AnGate.repository.TicketTypeRepository;
import io.angate.AnGate.service.TicketTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    @GetMapping("/events/{eventId}/ticket-types")
    public ResponseEntity<List<TicketTypeResponse>> getTicketsByEventId(@PathVariable long eventId){
        return ResponseEntity.ok(ticketTypeService.getTicketsByEventId(eventId));
    }

    @GetMapping("/ticket-types/{id}")
    public ResponseEntity<TicketTypeResponse> getTicketById(@PathVariable Long id){
        return ResponseEntity.ok(ticketTypeService.getTicketById(id));
    }

    @PostMapping("/events/{eventId}/ticket-types")
        public ResponseEntity<TicketTypeResponse> createTicketType(@RequestBody TicketTypeRequest ticketTypeRequest,
                                                                   @PathVariable Long eventId){
        return new ResponseEntity<>(ticketTypeService.createTicketType(ticketTypeRequest , eventId), HttpStatus.CREATED);
    }

    @DeleteMapping("/ticket-types/delete/{id}")
    public ResponseEntity<HttpStatus> deleteTicketType(@PathVariable Long id){
        return ResponseEntity.ok(ticketTypeService.deleteTicketType(id));
    }

    @PatchMapping("/ticket-types/update-ticket/{id}")
    public ResponseEntity<TicketTypeResponse> updatePartialTicket(@PathVariable Long id,
                                                                  @RequestBody TicketTypeRequest request ){
        return ResponseEntity.ok(ticketTypeService.updatePartialTicket(id,request));
    }
}
