package com.meetmint.meetmint_backend.Controller;


import com.meetmint.meetmint_backend.Dto.ApiResponseDTO;
import com.meetmint.meetmint_backend.Service.EventCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {
    private final EventCrudService eventService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<?>> getAllEvents(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        return eventService.getAllEvents(authHeader);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponseDTO<?>>  getEvent(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<ApiResponseDTO<?>>  getEventByTag(@PathVariable String tag) {
        return eventService.getEventByTag(tag);
    }


}
