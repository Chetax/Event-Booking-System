package com.meetmint.meetmint_backend.Controller;

import com.meetmint.meetmint_backend.Dto.UserRequestDto;
import com.meetmint.meetmint_backend.Dto.ApiResponseDTO;
import com.meetmint.meetmint_backend.Repository.UserRepository;
import com.meetmint.meetmint_backend.Service.JwtService;
import com.meetmint.meetmint_backend.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
   private final UserRepository userRepository;
   private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public UserController(UserService userService, JwtService jwtService, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<?>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        return userService.getCurrentUser(authHeader);
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<?>>createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        System.out.println(userRequestDto);
        return userService.createUser(userRequestDto);
    }

    @GetMapping()
    public ResponseEntity<ApiResponseDTO<?>> getMyBooking(){
        return userService.getMyBookings();
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<?>> loginUser(@RequestBody UserRequestDto loginRequest) {
          return userService.verifyUser(loginRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<?>> getUserById(@RequestBody UserRequestDto emailPassword) {
        return userService.getUserByEmailId(emailPassword);
    }


    @PutMapping("/{id}")

    public ResponseEntity<ApiResponseDTO<?>>updateUser(@PathVariable Long id,@RequestBody @Valid UserRequestDto oldUser,@RequestHeader("Authorization") String authHeader) {
        return userService.updateUser(id,oldUser,authHeader);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<ApiResponseDTO<?>>deleteUser(@PathVariable Long id,@RequestHeader("Authorization") String authHeader) {
        return  userService.deleteUser(id,authHeader);
    }


}
