package com.meetmint.meetmint_backend.Dto;

import com.meetmint.meetmint_backend.Model.Event;
import com.meetmint.meetmint_backend.Model.User;

import java.time.LocalDateTime;

public class TicketResponseDto {
    private String holderName;
    private String holderEmail;
    private Event event;
    private User user;
    private String eventTitle;
    private double ticketPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}



