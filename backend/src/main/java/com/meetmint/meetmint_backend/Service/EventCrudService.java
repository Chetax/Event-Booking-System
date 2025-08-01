package com.meetmint.meetmint_backend.Service;

import com.meetmint.meetmint_backend.Dto.EventRequestDto;
import com.meetmint.meetmint_backend.Dto.EventResponseDto;
import com.meetmint.meetmint_backend.Dto.ApiResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface EventCrudService {
    ResponseEntity<ApiResponseDTO<?>> createEvent(EventRequestDto dto);

    ResponseEntity<ApiResponseDTO<?>> getAllEvents(@RequestHeader(value = "Authorization", required = false) String authHeader);

    ResponseEntity<ApiResponseDTO<?>>  getEventById(Long id);

    ResponseEntity<ApiResponseDTO<?>> updateEvent(Long id, EventRequestDto dto);

    ResponseEntity<ApiResponseDTO<?>> deleteEvent(Long id);
    ResponseEntity<ApiResponseDTO<?>>  getEventByTag(@PathVariable String tag);

}
