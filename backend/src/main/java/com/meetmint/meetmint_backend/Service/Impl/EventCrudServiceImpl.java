package com.meetmint.meetmint_backend.Service.Impl;

import com.meetmint.meetmint_backend.CustomUserDetails;
import com.meetmint.meetmint_backend.Dto.EventRequestDto;
import com.meetmint.meetmint_backend.Dto.EventResponseDto;
import com.meetmint.meetmint_backend.Dto.ApiResponseDTO;
import com.meetmint.meetmint_backend.Model.Event;
import com.meetmint.meetmint_backend.Model.User;
import com.meetmint.meetmint_backend.Repository.EventRepository;
import com.meetmint.meetmint_backend.Repository.UserRepository;
import com.meetmint.meetmint_backend.Service.EventCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventCrudServiceImpl implements EventCrudService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponseDTO<?>> createEvent(EventRequestDto dto) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("\n\n\n\n\n\n\nEmail and role "+customUserDetails.getUsername()+" "+customUserDetails.getRole());
        boolean isOrganiser = customUserDetails.getRole();
        if (!isOrganiser) {
            ApiResponseDTO<String> apiResponseDTO = ApiResponseDTO.<String>builder()
                    .success(false)
                    .message("Not Authorize to visit this route")
                    .build();
            return ResponseEntity.status(401).body(apiResponseDTO);
        }

        User user = userRepository.findByEmail(customUserDetails.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Event event = Event.builder()
                .title(dto.getTitle())
                .tag(dto.getTag())
                .description(dto.getDescription())
                .eventImageUrl(dto.getEventImageUrl())
                .ticketCount(dto.getTicketCount())
                .ticketBooked(0)
                .price(dto.getPrice())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .createdBy(user)
                .build();

        try {
            Event saved = eventRepository.save(event);
            // Optionally add event to user's event list (maintain both sides)
            if (user.getEvents() == null) {
                user.setEvents(new ArrayList<>());
            }
            user.getEvents().add(saved);
            userRepository.save(user);

            ApiResponseDTO<Event> apiResponseDTO = ApiResponseDTO.<Event>builder()
                    .success(true)
                    .message("Event created successfully")
                    .data(saved)
                    .build();

            return ResponseEntity.ok(apiResponseDTO);

        } catch (Exception exception) {
            ApiResponseDTO<String> apiResponseDTO = ApiResponseDTO.<String>builder()
                    .success(false)
                    .message(exception.getMessage())
                    .build();
            return ResponseEntity.status(403).body(apiResponseDTO);
        }
    }

    @Override
    public ResponseEntity<ApiResponseDTO<?>> getAllEvents(@RequestHeader(value = "Authorization", required = false) String authHeader) {



        List<EventResponseDto> events= eventRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());


        ApiResponseDTO<List<EventResponseDto>> apiResponseDTO1=ApiResponseDTO.<List<EventResponseDto>>builder()
                .success(true)
                .message("Event data fetched successfully")
                .data(events)
                .token(authHeader)
                .build();

        return ResponseEntity.ok().body(apiResponseDTO1);
    }

    @Override
    public ResponseEntity<ApiResponseDTO<?>> getEventById(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        EventResponseDto eventResponseDto = EventResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventImageUrl(event.getEventImageUrl())
                .ticketCount(event.getTicketCount())
                .ticketBooked(event.getTicketBooked())
                .tag(event.getTag())
                .price(event.getPrice())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .createrEmail(event.getCreatedBy().getEmail())
                .createdBy(event.getCreatedBy().getFirstName()+" "+event.getCreatedBy().getLastName()) // this line includes the user
                .build();

        ApiResponseDTO<EventResponseDto> apiResponseDTO = ApiResponseDTO.<EventResponseDto>builder()
                .success(true)
                .message("Successfully fetched")
                .data(eventResponseDto)
                .build();
        return ResponseEntity.ok(apiResponseDTO);
    }

    @Override
    public ResponseEntity<ApiResponseDTO<?>> updateEvent(Long id, EventRequestDto dto) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(404).body(ApiResponseDTO.builder()
                    .success(false)
                    .message("Event not found")
                    .build());
        }

        Event event = optionalEvent.get();

        if (event.getCreatedBy() == null || event.getCreatedBy().getId() != customUserDetails.getId()) {
            return ResponseEntity.status(403).body(ApiResponseDTO.builder()
                    .success(false)
                    .message("You are not authorized to update this event")
                    .build());
        }

        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getTag() != null) event.setTag(dto.getTag());
        if (dto.getPrice() != null) event.setPrice(dto.getPrice());
        if (dto.getStartTime() != null) event.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) event.setEndTime(dto.getEndTime());
        if (dto.getEventImageUrl() != null) event.setEventImageUrl(dto.getEventImageUrl());
        if (dto.getTicketCount() != null) event.setTicketCount(dto.getTicketCount());
        if (dto.getTicketBooked() != null) event.setTicketBooked(dto.getTicketBooked());

        try {
            Event updatedEvent = eventRepository.save(event);
            return ResponseEntity.ok(ApiResponseDTO.<Event>builder()
                    .success(true)
                    .message("Event updated successfully")
                    .data(updatedEvent)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponseDTO.<String>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }


    @Override
    public ResponseEntity<ApiResponseDTO<?>> deleteEvent(Long id) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!eventRepository.existsById(id)) {
            Optional<Event> optionalEvent = eventRepository.findById(id);
            if (optionalEvent.isEmpty()) {
                return ResponseEntity.status(401).body(ApiResponseDTO.builder()
                        .success(false)
                        .message("Not Authroize person to delete this event ")
                        .build());
            }

            Event event = optionalEvent.get();
            if (event.getCreatedBy() == null || event.getCreatedBy().getId() != customUserDetails.getId()) {
                return ResponseEntity.status(403).body(ApiResponseDTO.builder()
                        .success(false)
                        .message("You are not authorized to update this event")
                        .build());
            }

            ApiResponseDTO<String> apiResponseDTO=ApiResponseDTO.<String>builder()
                    .success(false)
                    .message("Event not exist")
                    .build();
            return ResponseEntity.status(404).body(apiResponseDTO);
        }
        eventRepository.deleteById(id);
        ApiResponseDTO<String> apiResponseDTO=ApiResponseDTO.<String>builder()
                .success(true)
                .message("Event deleted successfully")
                .build();
        return ResponseEntity.status(200).body(apiResponseDTO);
    }

@Override
public ResponseEntity<ApiResponseDTO<?>> getEventByTag(@PathVariable String tag){
        List<Event> events=eventRepository.findByTag(tag);
    ApiResponseDTO<List<Event>> apiResponseDTO=ApiResponseDTO.<List<Event>>builder()
            .success(true)
            .message("Event Fetched successfully by Tag")
            .data(events)
            .build();
    return ResponseEntity.status(200).body(apiResponseDTO);
}

    private EventResponseDto mapToDto(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventImageUrl(event.getEventImageUrl())
                .ticketCount(event.getTicketCount())
                .ticketBooked(event.getTicketBooked())
                .price(event.getPrice())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .build();
    }

}
