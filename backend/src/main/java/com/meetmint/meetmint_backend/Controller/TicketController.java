package com.meetmint.meetmint_backend.Controller;

import com.meetmint.meetmint_backend.Dto.ApiResponseDTO;
import com.meetmint.meetmint_backend.Dto.TicketRequestDto;
import com.meetmint.meetmint_backend.Dto.TicketResponseDto;
import com.meetmint.meetmint_backend.Service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<?>> createTicket(@RequestBody TicketRequestDto ticketRequestDto){
       return ticketService.createTicket(ticketRequestDto);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<?>> getTicketById(@PathVariable Long id){
        return ticketService.getTicketById(id);
    }

    @GetMapping
    public  ResponseEntity<ApiResponseDTO<?>> getAllTickets()
    {
        return ticketService.getAllTicket();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponseDTO<?>> getTicketByEmail(@PathVariable  String email){
        return  ticketService.getTicketByEmail(email);
    }

    @GetMapping("/eventTicketCheck/{id}")
    public ResponseEntity<ApiResponseDTO<?>> checkTicketAvailibilityByEventId(@PathVariable Long id){
        return ticketService.checkTicketAvailibilityByEventId(id);
    }

    @GetMapping("/recentBooking")
    public ResponseEntity<ApiResponseDTO<?>> getMyRecentTicketBooking(){
        return ticketService.getMyTickets();
    }

    @DeleteMapping("/cancelMyBooking/{id}")
    public ResponseEntity<ApiResponseDTO<?>> cancelMyTicket(@PathVariable long id){
     return ticketService.deleteMyTicket(id);
    }


}
