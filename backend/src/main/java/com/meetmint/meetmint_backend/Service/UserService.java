package com.meetmint.meetmint_backend.Service;

import com.meetmint.meetmint_backend.Dto.UserRequestDto;
import com.meetmint.meetmint_backend.Dto.ApiResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {
    ResponseEntity<ApiResponseDTO<?>> createUser(UserRequestDto requestDto);
    ResponseEntity<ApiResponseDTO<?>> getUserByEmailId(UserRequestDto emailAndPassword);
    ResponseEntity<ApiResponseDTO<?>> updateUser(Long id, UserRequestDto userRequestDto,String authHeader);
    ResponseEntity<ApiResponseDTO<?>> deleteUser(Long id,String authHeader);
    ResponseEntity<ApiResponseDTO<?>> verifyUser(@RequestBody UserRequestDto loginRequest);
    ResponseEntity<ApiResponseDTO<?>> getMyBookings();
    ResponseEntity<ApiResponseDTO<?>> getMyTicket(long id);
    ResponseEntity<ApiResponseDTO<?>>getAllTicketByEventId(long id,String authHeader);
}
