package com.meetmint.meetmint_backend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetmint.meetmint_backend.Dto.ApiResponseDTO;
import com.meetmint.meetmint_backend.Dto.EventResponseDto;
import com.meetmint.meetmint_backend.Model.Event;
import com.meetmint.meetmint_backend.Service.EventCrudService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventCrudService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllEvents() throws Exception {
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjp0cnVlLCJzdWIiOiJkb2phQGNvbnN1bHRhZGQuY29tIiwiaXNzIjoiRG9qYSBDYXQiLCJpYXQiOjE3NTM2Nzg3MTksImV4cCI6MTc1MzY4MDUxOX0.VTxLvQxkaTX6NJ2tPAUcOkBAHFz5A5ERtoRiPFlSYAg";
        EventResponseDto event1 = EventResponseDto.builder()
                .id(1L)
                .title("Tech Summit")
                .description("An event for techies.")
                .eventImageUrl("image1.jpg")
                .tag("tech")
                .ticketCount(100)
                .ticketBooked(20)
                .price(99.99)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .build();

        List<EventResponseDto> events = List.of(event1);

        ApiResponseDTO<List<EventResponseDto>> apiResponseDTO = ApiResponseDTO.<List<EventResponseDto>>builder()
                .success(true)
                .message("Event data fetched successfully")
                .data(events)
                .token(token)
                .build();

        Mockito.when(eventService.getAllEvents(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok(apiResponseDTO));

        mockMvc.perform(get("/api/event")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Event data fetched successfully"))
                .andExpect(jsonPath("$.data[0].title").value("Tech Summit"))
                .andExpect(jsonPath("$.token").value(token));

        Mockito.verify(eventService, Mockito.times(1)).getAllEvents(token);
    }

    @Test
    void testGetEventById() throws Exception {
        Long eventId = 1L;

        EventResponseDto event = EventResponseDto.builder()
                .id(1L)
                .title("Tech Summit")
                .description("An event for techies.")
                .eventImageUrl("image1.jpg")
                .tag("tech")
                .ticketCount(100)
                .ticketBooked(20)
                .price(99.99)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .createdBy("John Doe")
                .createrEmail("john@example.com")
                .build();

        ApiResponseDTO<EventResponseDto> apiResponse = ApiResponseDTO.<EventResponseDto>builder()
                .success(true)
                .message("Successfully fetched")
                .data(event)
                .build();

        Mockito.when(eventService.getEventById(eventId))
                .thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(get("/api/event/id/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Tech Summit"));

        Mockito.verify(eventService, Mockito.times(1)).getEventById(eventId);
    }

    @Test
    void testGetEventByTag() throws Exception {
        String tag = "tech";

        Event event = Event.builder()
                .id(1L)
                .title("Tech Summit")
                .description("An event for techies.")
                .eventImageUrl("image1.jpg")
                .tag("tech")
                .ticketCount(100)
                .ticketBooked(20)
                .price(99.99)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .build();

        ApiResponseDTO<List<Event>> apiResponse = ApiResponseDTO.<List<Event>>builder()
                .success(true)
                .message("Event Fetched successfully by Tag")
                .data(List.of(event))
                .build();

        Mockito.when(eventService.getEventByTag(tag))
                .thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(get("/api/event/tag/{tag}", tag))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].tag").value("tech"));

        Mockito.verify(eventService, Mockito.times(1)).getEventByTag(tag);
    }


}
