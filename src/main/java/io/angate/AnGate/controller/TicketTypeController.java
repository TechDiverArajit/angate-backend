package io.angate.AnGate.controller;

import io.angate.AnGate.dto.tickettype.CreateTicketTypeRequest;
import io.angate.AnGate.dto.tickettype.TicketTypeRequest;
import io.angate.AnGate.dto.tickettype.TicketTypeResponse;
import io.angate.AnGate.repository.TicketTypeRepository;
import io.angate.AnGate.service.TicketTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/events/{eventId}/ticket-types")
    public ResponseEntity<List<TicketTypeResponse>> getTicketsByEventId(@PathVariable long eventId){
        return ResponseEntity.ok(ticketTypeService.getTicketsByEventId(eventId));
    }
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/ticket-types/{id}")
    public ResponseEntity<TicketTypeResponse> getTicketById(@PathVariable Long id){
        return ResponseEntity.ok(ticketTypeService.getTicketById(id));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/events/{eventId}/ticket-types")
        public ResponseEntity<TicketTypeResponse> createTicketType(@RequestBody @Valid CreateTicketTypeRequest ticketTypeRequest,
                                                                   @PathVariable Long eventId){
        return new ResponseEntity<>(ticketTypeService.createTicketType(ticketTypeRequest , eventId), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/ticket-types/{id}")
    public ResponseEntity<Void> deleteTicketType(@PathVariable Long id) throws BadRequestException {
        ticketTypeService.deleteTicketType(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/ticket-types/{id}")
    public ResponseEntity<TicketTypeResponse> updatePartialTicket(@PathVariable Long id, @RequestBody @Valid TicketTypeRequest request ){
        return ResponseEntity.ok(ticketTypeService.updatePartialTicket(id,request));
    }
}
