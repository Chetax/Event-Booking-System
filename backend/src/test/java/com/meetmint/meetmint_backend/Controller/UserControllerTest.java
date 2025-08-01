package com.meetmint.meetmint_backend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetmint.meetmint_backend.Controller.UserController;
import com.meetmint.meetmint_backend.Dto.ApiResponseDTO;
import com.meetmint.meetmint_backend.Dto.UserRequestDto;
import com.meetmint.meetmint_backend.Model.Ticket;
import com.meetmint.meetmint_backend.Model.User;
import com.meetmint.meetmint_backend.Repository.UserRepository;
import com.meetmint.meetmint_backend.Service.Impl.UserServiceImpl;
import com.meetmint.meetmint_backend.Service.JwtService;
import com.meetmint.meetmint_backend.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @InjectMocks
    private UserController userController;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequestDto userDto;

    @BeforeEach
    void setUp(){
        userDto=new UserRequestDto();
        userDto.setEmail("chetax@consultadd.com");
        userDto.setPassword("chex123");
    }


    @Test
    void whenNoAuthenticatedThenShouldReturnUnauthorized() throws Exception{
       mockMvc.perform(get("/api/users/getHome"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegister() throws Exception {
        String randomEmail = "john.doe" + UUID.randomUUID().toString().substring(0, 5) + "@example.com";

        UserRequestDto userRequestDto = UserRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email(randomEmail)
                .password("Password123")
                .Organiser(true)
                .profilePhotoUrl("http://example.com/photo.jpg")
                .build();

        User mockUser = User.builder()
                .id(12345L)
                .firstName("John")
                .lastName("Doe")
                .email(randomEmail)
                .Organiser(true)
                .profilePhotoUrl("http://example.com/photo.jpg")
                .build();

        ApiResponseDTO<User> responseDto = ApiResponseDTO.<User>builder()
                .success(true)
                .message("User registered successfully")
                .data(mockUser)
                .build();

        ResponseEntity<ApiResponseDTO<?>> mockResponse = ResponseEntity.status(201).body(responseDto);

        Mockito.when(userService.createUser(Mockito.any(UserRequestDto.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.id").value(12345))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.organiser").value(true));

        Mockito.verify(userService, Mockito.times(1)).createUser(Mockito.any(UserRequestDto.class));
    }


    @Test
    @WithMockUser(username = "chetax@consultadd.com", roles = {"USER"})
    void testGetMyBooking() throws Exception {
        List<Ticket> mockTickets = List.of(
                Ticket.builder()
                        .id(1L)
                        .holderName("Chetan Padhen")
                        .holderEmail("chetan@consultadd.com")
                        .build(),

                Ticket.builder()
                        .id(2L)
                        .holderName("Hasan Raheem ")
                        .holderEmail("Hasan@consultadd.com")
                        .build()
        );


        ApiResponseDTO<List<Ticket>> responseDto = ApiResponseDTO.<List<Ticket>>builder()
                .success(true)
                .message("All tickets are fetched ")
                .data(mockTickets)
                .build();

        Mockito.when(userService.getMyBookings())
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(get("/api/users")  // adjust path to your actual endpoint
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All tickets are fetched "))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2));

        Mockito.verify(userService, Mockito.times(1)).getMyBookings();
    }

    @Test
    void testLoginUser() throws Exception
    {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("chetax@consultadd.com")
                .password("chex123")
                .build();


        User mockUser = User.builder()
                .id(12345L)
                .firstName("John")
                .lastName("Doe")
                .email("chetax@consultadd.com")
                .Organiser(true)
                .profilePhotoUrl("http://example.com/photo.jpg")
                .build();


        ApiResponseDTO<User> response = ApiResponseDTO.<User>builder()
                .success(true)
                .message("Login Success")
                .data(mockUser)
                .token("eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjpmYWxzZSwic3ViIjoiYWxpQGNvbnN1bHRhZGQuY29tIiwiaXNzIjoiQWxpIFphZmFyIiwiaWF0IjoxNzUzNjIzNjI5LCJleHAiOjE3NTM2MjU0Mjl9.7EiXA6LO4Y5eJDLTnZbCIzF8Ilv0FD4wpb8YB1M9G20")
                .build();

        ResponseEntity<ApiResponseDTO<?>> mockResponse = ResponseEntity.status(201).body(response);
        Mockito.when(userService.verifyUser(Mockito.any(UserRequestDto.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Login Success"))
                .andExpect(jsonPath("$.data.id").value(12345))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.email").value("chetax@consultadd.com"))
                .andExpect(jsonPath("$.data.organiser").value(true))
                .andExpect(jsonPath("$.data.profilePhotoUrl").value("http://example.com/photo.jpg"))
                .andExpect(jsonPath("$.token").value(response.getToken()));

        Mockito.verify(userService, Mockito.times(1)).verifyUser(Mockito.any(UserRequestDto.class));

    }
    @Test
    @WithMockUser(username = "chetax@consultadd.com", roles = {"USER"})
    void testGetUserById() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email("test@example.com")
                .password("password")
                .build();

        User mockUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .Organiser(false)
                .build();

        ApiResponseDTO<User> apiResponse = ApiResponseDTO.<User>builder()
                .success(true)
                .message("User fetched successfully")
                .data(mockUser)
                .build();

        ResponseEntity<ApiResponseDTO<?>> responseEntity = ResponseEntity.ok(apiResponse);

        Mockito.when(userService.getUserByEmailId(Mockito.any(UserRequestDto.class)))
                .thenReturn(responseEntity);

        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("Test"));

        Mockito.verify(userService, Mockito.times(1)).getUserByEmailId(Mockito.any(UserRequestDto.class));
    }


    @Test
    @WithMockUser(username = "chetax@consultadd.com", roles = {"USER"})
    void testUpdateUser() throws Exception {
        Long userId = 1L;
        String authHeader = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjp0cnVlLCJzdWIiOiJkb2phQGNvbnN1bHRhZGQuY29tIiwiaXNzIjoiRG9qYSBDYXQiLCJpYXQiOjE3NTM2Nzg3MTksImV4cCI6MTc1MzY4MDUxOX0.VTxLvQxkaTX6NJ2tPAUcOkBAHFz5A5ERtoRiPFlSYAg";

        UserRequestDto updateUserDto = UserRequestDto.builder()
                .firstName("Updated")
                .lastName("User")
                .email("updated@example.com")
                .password("newpass123")
                .Organiser(true)
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .firstName("Updated")
                .lastName("User")
                .email("updated@example.com")
                .Organiser(true)
                .build();

        ApiResponseDTO<User> apiResponse = ApiResponseDTO.<User>builder()
                .success(true)
                .message("User updated successfully")
                .data(updatedUser)
                .build();

        ResponseEntity<ApiResponseDTO<?>> responseEntity = ResponseEntity.ok(apiResponse);

        Mockito.when(userService.updateUser(
                        Mockito.eq(userId),
                        Mockito.any(UserRequestDto.class),
                        Mockito.anyString()))
                .thenReturn(responseEntity);

        mockMvc.perform(put("/api/users/{id}", userId)
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.firstName").value("Updated"))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"));

        Mockito.verify(userService, Mockito.times(1))
                .updateUser(Mockito.eq(userId), Mockito.any(UserRequestDto.class), Mockito.eq(authHeader));
    }


    // 3. Test for DELETE /{id}
    @Test
    @WithMockUser(username = "chetax@consultadd.com", roles = {"USER"})
    void testDeleteUser() throws Exception {
        Long userId = 1L;
        String authHeader = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjp0cnVlLCJzdWIiOiJkb2phQGNvbnN1bHRhZGQuY29tIiwiaXNzIjoiRG9qYSBDYXQiLCJpYXQiOjE3NTM2Nzg3MTksImV4cCI6MTc1MzY4MDUxOX0.VTxLvQxkaTX6NJ2tPAUcOkBAHFz5A5ERtoRiPFlSYAg";

        ApiResponseDTO<String> apiResponse = ApiResponseDTO.<String>builder()
                .success(true)
                .message("User deleted successfully")
                .data(null)
                .build();

        ResponseEntity<ApiResponseDTO<?>> responseEntity = ResponseEntity.ok(apiResponse);

        // Mock the method with both arguments
        Mockito.when(userService.deleteUser(Mockito.eq(userId), Mockito.eq(authHeader))).thenReturn(responseEntity);

        mockMvc.perform(delete("/api/users/{id}", userId)
                        .header("Authorization", authHeader)) // Include authHeader in the request
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        Mockito.verify(userService, Mockito.times(1)).deleteUser(Mockito.eq(userId), Mockito.eq(authHeader));
    }

}

