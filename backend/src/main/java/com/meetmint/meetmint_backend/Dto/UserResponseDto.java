package com.meetmint.meetmint_backend.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private boolean isOrganiser;
    private String profilePhotoUrl;
    private String email;
}



