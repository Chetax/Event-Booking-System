package com.meetmint.meetmint_backend.Dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestDto {
    private String title;
    private String description;
    private String eventImageUrl;
    private String tag;

    private Integer ticketCount;
    private Integer ticketBooked;
    private Double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
