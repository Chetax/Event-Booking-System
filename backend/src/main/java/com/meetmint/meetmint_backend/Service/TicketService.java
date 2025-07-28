package com.meetmint.meetmint_backend.Service;

import com.meetmint.meetmint_backend.Dto.ApiResponseDTO;
import com.meetmint.meetmint_backend.Dto.TicketRequestDto;
import com.meetmint.meetmint_backend.Dto.TicketResponseDto;
import com.meetmint.meetmint_backend.Dto.UserRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface TicketService {
    ResponseEntity<ApiResponseDTO<?>> createTicket(TicketRequestDto ticketRequestDto);
    ResponseEntity<ApiResponseDTO<?>> getTicketById(@PathVariable Long id );
    ResponseEntity<ApiResponseDTO<?>> getAllTicket();
    ResponseEntity<ApiResponseDTO<?>> getTicketByEmail(String email);
    ResponseEntity<ApiResponseDTO<?>> checkTicketAvailibilityByEventId(@PathVariable long id);
    ResponseEntity<ApiResponseDTO<?>> getMyTickets();
    ResponseEntity<ApiResponseDTO<?>> getMyEventRegister();
    ResponseEntity<ApiResponseDTO<?>> deleteMyTicket(long id);

}

