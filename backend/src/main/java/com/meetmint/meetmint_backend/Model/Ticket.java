package com.meetmint.meetmint_backend.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String holderName;

    @Email
    private String holderEmail;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = true)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @NotBlank
    private String eventTitle;

    @DecimalMin("0.0")
    private double ticketPrice;

    @FutureOrPresent
    private LocalDateTime startTime;

    @Future
    private LocalDateTime endTime;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

}
