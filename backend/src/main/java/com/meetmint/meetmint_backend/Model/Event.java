package com.meetmint.meetmint_backend.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @NotBlank
    private String title;


    @NotBlank
    private String description ;
    private String eventImageUrl ;

    @NotBlank
    private String tag;

    @Positive
    private Integer ticketCount ;

    @PositiveOrZero
    private Integer ticketBooked ;

    @DecimalMin("0.0")
    private Double price ;

    @NotBlank
    private String location;

    @FutureOrPresent
    private LocalDateTime startTime ;

    @Future
    private LocalDateTime endTime ;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonBackReference  // <-- Add this
    private User createdBy;
}
