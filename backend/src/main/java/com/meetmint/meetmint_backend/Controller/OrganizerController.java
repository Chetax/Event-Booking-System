package com.meetmint.meetmint_backend.Controller;

import com.meetmint.meetmint_backend.Dto.EventRequestDto;
import com.meetmint.meetmint_backend.Dto.EventResponseDto;
import com.meetmint.meetmint_backend.Dto.ApiResponseDTO;
import com.meetmint.meetmint_backend.Service.EventCrudService;
import com.meetmint.meetmint_backend.Service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/organizer/events")
@RequiredArgsConstructor

public class OrganizerController {

    private final EventCrudService eventService;
    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<?>> createEvent(@Valid @RequestBody EventRequestDto dto) {
        return eventService.createEvent(dto);
    }

    @GetMapping
    public  ResponseEntity<ApiResponseDTO<?>> getAllEvents(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        return eventService.getAllEvents(authHeader);
    }

@GetMapping("/getMyRegister")
public  ResponseEntity<ApiResponseDTO<?>>getMyEventRegister(){
        return ticketService.getMyEventRegister();
}

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<?>>  getEvent(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @PutMapping("/{id}")
    public     ResponseEntity<ApiResponseDTO<?>> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequestDto dto) {
        return eventService.updateEvent(id, dto);
    }


    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}
