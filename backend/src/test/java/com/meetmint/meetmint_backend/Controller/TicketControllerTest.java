package com.meetmint.meetmint_backend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetmint.meetmint_backend.Dto.ApiResponseDTO;
import com.meetmint.meetmint_backend.Dto.TicketRequestDto;
import com.meetmint.meetmint_backend.Model.Ticket;
import com.meetmint.meetmint_backend.Service.TicketService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TicketController.class)
@AutoConfigureMockMvc(addFilters = false)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateTicket() throws Exception {
        TicketRequestDto requestDto = TicketRequestDto.builder()
                .eventId(1L)
                .userId(2L)
                .build();

        Ticket mockTicket = Ticket.builder()
                .holderName("John Doe")
                .holderEmail("john@example.com")
                .ticketPrice(100.0)
                .eventTitle("Mock Event")
                .build();

        ApiResponseDTO<Ticket> responseDto = ApiResponseDTO.<Ticket>builder()
                .success(true)
                .message("Ticket Created Successfully")
                .data(mockTicket)
                .build();

        Mockito.when(ticketService.createTicket(Mockito.any()))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(post("/api/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Ticket Created Successfully"))
                .andExpect(jsonPath("$.data.holderName").value("John Doe"));
    }

    @Test
    void testGetTicketById() throws Exception {
        Long ticketId = 1L;

        Ticket ticket = Ticket.builder()
                .holderName("Jane Smith")
                .holderEmail("jane@example.com")
                .build();

        ApiResponseDTO<Ticket> response = ApiResponseDTO.<Ticket>builder()
                .success(true)
                .message("Ticket fetched successfully")
                .data(ticket)
                .build();

        Mockito.when(ticketService.getTicketById(ticketId))
                .thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(get("/api/ticket/{id}", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.holderEmail").value("jane@example.com"));
    }

    @Test
    void testGetAllTickets() throws Exception {
        ApiResponseDTO<List<Ticket>> response = ApiResponseDTO.<List<Ticket>>builder()
                .success(true)
                .message("ticket fetch successfully")
                .data(Collections.emptyList())
                .build();

        Mockito.when(ticketService.getAllTicket())
                .thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(get("/api/ticket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ticket fetch successfully"));
    }

    @Test
    void testGetTicketByEmail() throws Exception {
        String email = "test@example.com";

        ApiResponseDTO<List<Ticket>> response = ApiResponseDTO.<List<Ticket>>builder()
                .success(true)
                .message("All tickets are fetched ")
                .data(Collections.emptyList())
                .build();

        Mockito.when(ticketService.getTicketByEmail(email))
                .thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(get("/api/ticket/email/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All tickets are fetched "));
    }

    @Test
    void testCheckTicketAvailability() throws Exception {
        Long eventId = 1L;

        ApiResponseDTO<Integer> response = ApiResponseDTO.<Integer>builder()
                .success(true)
                .message("Tickets Availiblty fatched")
                .data(5)
                .build();

        Mockito.when(ticketService.checkTicketAvailibilityByEventId(eventId))
                .thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(get("/api/ticket/eventTicketCheck/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(5));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetMyRecentTicketBooking() throws Exception {
        ApiResponseDTO<List<Ticket>> response = ApiResponseDTO.<List<Ticket>>builder()
                .success(true)
                .message("Fetched tickets in reverse order")
                .data(Collections.emptyList())
                .build();

        Mockito.when(ticketService.getMyTickets())
                .thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(get("/api/ticket/recentBooking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Fetched tickets in reverse order"));
    }

    @Test
    void testCancelMyTicket() throws Exception {
        long ticketId = 1L;

        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(true)
                .message("Booking deleted successfully")
                .build();

        Mockito.when(ticketService.deleteMyTicket(ticketId))
                .thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(delete("/api/ticket/cancelMyBooking/{id}", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Booking deleted successfully"));
    }
}
