package com.meetmint.meetmint_backend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetmint.meetmint_backend.CustomUserDetails;
import com.meetmint.meetmint_backend.Dto.ApiResponseDTO;
import com.meetmint.meetmint_backend.Dto.EventRequestDto;
import com.meetmint.meetmint_backend.Dto.EventResponseDto;
import com.meetmint.meetmint_backend.Model.Event;
import com.meetmint.meetmint_backend.Model.Ticket;
import com.meetmint.meetmint_backend.Service.Impl.EventCrudServiceImpl;
import com.meetmint.meetmint_backend.Service.Impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class OrganizerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventCrudServiceImpl eventService;

    @MockBean
    private TicketServiceImpl ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomUserDetails mockUserDetails;

    @BeforeEach
    void setupSecurityContext() {
        // Mock authenticated user
        mockUserDetails = Mockito.mock(CustomUserDetails.class);
        Mockito.when(mockUserDetails.getId()).thenReturn(1L);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("user@example.com");

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getPrincipal()).thenReturn(mockUserDetails);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    void testCreateEventAsOrganiser() throws Exception {
        EventRequestDto eventRequestDto = EventRequestDto.builder()
                .title("Tech Conference")
                .description("Spring Boot 2025")
                .eventImageUrl("http://example.com/image.jpg")
                .tag("Technology")
                .ticketCount(200)
                .ticketBooked(0)
                .price(99.99)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .build();

        Event savedEvent = Event.builder()
                .id(1L)
                .title(eventRequestDto.getTitle())
                .description(eventRequestDto.getDescription())
                .eventImageUrl(eventRequestDto.getEventImageUrl())
                .tag(eventRequestDto.getTag())
                .ticketCount(eventRequestDto.getTicketCount())
                .ticketBooked(0)
                .price(eventRequestDto.getPrice())
                .startTime(eventRequestDto.getStartTime())
                .endTime(eventRequestDto.getEndTime())
                .build();

        ApiResponseDTO<Event> responseDto = ApiResponseDTO.<Event>builder()
                .success(true)
                .message("Event created successfully")
                .data(savedEvent)
                .build();

        Mockito.when(eventService.createEvent(any(EventRequestDto.class)))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(post("/api/organizer/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Event created successfully"))
                .andExpect(jsonPath("$.data.title").value("Tech Conference"));

        Mockito.verify(eventService, Mockito.times(1)).createEvent(any(EventRequestDto.class));
    }

    @Test
    void testGetAllEvents() throws Exception {

        EventResponseDto event1 = EventResponseDto.builder()
                .title("Tech Conference")
                .description("Spring Boot 2025")
                .eventImageUrl("http://example.com/image1.jpg")
                .tag("Technology")
                .ticketCount(100)
                .ticketBooked(10)
                .price(50.0)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .build();

        EventResponseDto event2 = EventResponseDto.builder()
                .title("Music Festival")
                .description("Summer Music Festival 2025")
                .eventImageUrl("http://example.com/image2.jpg")
                .tag("Music")
                .ticketCount(500)
                .ticketBooked(300)
                .price(75.0)
                .startTime(LocalDateTime.now().plusDays(5))
                .endTime(LocalDateTime.now().plusDays(6))
                .build();

        List<EventResponseDto> events = List.of(event1, event2);

        String fakeAuthHeader = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjpmYWxzZSwic3ViIjoiYWxpQGNvbnN1bHRhZGQuY29tIiwiaXNzIjoiQWxpIFphZmFyIiwiaWF0IjoxNzUzNjIzNjI5LCJleHAiOjE3NTM2MjU0Mjl9.7EiXA6LO4Y5eJDLTnZbCIzF8Ilv0FD4wpb8YB1M9G20";

        ApiResponseDTO<List<EventResponseDto>> apiResponseDTO = ApiResponseDTO.<List<EventResponseDto>>builder()
                .success(true)
                .message("Event data fetched successfully")
                .data(events)
                .token(fakeAuthHeader)
                .build();


        Mockito.when(eventService.getAllEvents(Mockito.eq(fakeAuthHeader)))
                .thenReturn(ResponseEntity.ok(apiResponseDTO));


        mockMvc.perform(get("/api/organizer/events")
                        .header("Authorization", fakeAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Event data fetched successfully"))
                .andExpect(jsonPath("$.token").value(fakeAuthHeader))
                .andExpect(jsonPath("$.data[0].title").value("Tech Conference"))
                .andExpect(jsonPath("$.data[1].title").value("Music Festival"));

        Mockito.verify(eventService, Mockito.times(1)).getAllEvents(fakeAuthHeader);

    }

    @Test
    void testGetMyEventRegister() throws Exception {

        CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("user@example.com");

        Authentication mockAuth = Mockito.mock(Authentication.class);
        Mockito.when(mockAuth.getPrincipal()).thenReturn(mockUserDetails);


        SecurityContext mockSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);

        SecurityContextHolder.setContext(mockSecurityContext);

        List<Ticket> mockTickets = List.of();

        ApiResponseDTO<List<Ticket>> responseDto = ApiResponseDTO.<List<Ticket>>builder()
                .success(true)
                .message("Fetched tickets in reverse order")
                .data(mockTickets)
                .build();

        Mockito.when(ticketService.getMyEventRegister())
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(get("/api/organizer/events/getMyRegister"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Fetched tickets in reverse order"))
                .andExpect(jsonPath("$.data").isArray());

        Mockito.verify(ticketService, Mockito.times(1)).getMyEventRegister();
    }

    @Test
    void testGetEventById() throws Exception {
        Event event = Event.builder()
                .id(1L)
                .title("Sample Event")
                .description("Test description")
                .build();

        ApiResponseDTO<Event> responseDto = ApiResponseDTO.<Event>builder()
                .success(true)
                .message("sucessfully fetched")
                .data(event)
                .build();

        Mockito.when(eventService.getEventById(1L))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(get("/api/organizer/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("sucessfully fetched"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Sample Event"));

        Mockito.verify(eventService, Mockito.times(1)).getEventById(1L);
    }

    @Test
    void testUpdateEvent() throws Exception {
        EventRequestDto updateDto = EventRequestDto.builder()
                .title("Updated Event Title")
                .description("Updated description")
                .build();

        Event updatedEvent = Event.builder()
                .id(1L)
                .title(updateDto.getTitle())
                .description(updateDto.getDescription())
                .build();

        ApiResponseDTO<Event> responseDto = ApiResponseDTO.<Event>builder()
                .success(true)
                .message("Event updated successfully")
                .data(updatedEvent)
                .build();

        Mockito.when(eventService.updateEvent(anyLong(), any(EventRequestDto.class)))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(put("/api/organizer/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Event updated successfully"))
                .andExpect(jsonPath("$.data.title").value("Updated Event Title"))
                .andExpect(jsonPath("$.data.description").value("Updated description"));

        Mockito.verify(eventService, Mockito.times(1))
                .updateEvent(anyLong(), any(EventRequestDto.class));
    }


}