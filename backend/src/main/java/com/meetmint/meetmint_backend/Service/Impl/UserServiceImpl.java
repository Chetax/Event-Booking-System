package com.meetmint.meetmint_backend.Service.Impl;

import com.meetmint.meetmint_backend.CustomUserDetails;
import com.meetmint.meetmint_backend.Dto.EventRequestDto;
import com.meetmint.meetmint_backend.Dto.UserRequestDto;
import com.meetmint.meetmint_backend.Dto.UserResponseDto;
import com.meetmint.meetmint_backend.Dto.ApiResponseDTO;
import com.meetmint.meetmint_backend.Model.Event;
import com.meetmint.meetmint_backend.Model.Ticket;
import com.meetmint.meetmint_backend.Model.User;
import com.meetmint.meetmint_backend.Repository.EventRepository;
import com.meetmint.meetmint_backend.Repository.TicketRepository;
import com.meetmint.meetmint_backend.Repository.UserRepository;
import com.meetmint.meetmint_backend.Service.JwtService;
import com.meetmint.meetmint_backend.Service.TicketService;
import com.meetmint.meetmint_backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final TicketRepository ticketRepository;
    private final TicketServiceImpl ticketService;
    private final EventRepository eventRepository;
    private final EventCrudServiceImpl eventCrudService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtService jwtService, TicketRepository ticketRepository, TicketServiceImpl ticketService, EventRepository eventRepository, EventCrudServiceImpl eventCrudService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.ticketRepository = ticketRepository;
        this.ticketService = ticketService;
        this.eventRepository = eventRepository;
        this.eventCrudService = eventCrudService;
    }

    @Override
    public ResponseEntity<ApiResponseDTO<?>> getCurrentUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);  // or userId if stored

        User user = userRepository.findByEmail(email);
        ApiResponseDTO<User> apiResponseDTO=ApiResponseDTO.<User>builder()
                .success(true)
                .message("User fetched successfully")
                .data(user)
                .build();

        return ResponseEntity.ok(apiResponseDTO);
    }

    @Override
    public ResponseEntity<ApiResponseDTO<?>> createUser(UserRequestDto userRequestDto) {
        System.out.println(userRequestDto.isOrganiser());
        User user = User.builder()
                .firstName(userRequestDto.getFirstName())
                .lastName(userRequestDto.getLastName())
                .Organiser(userRequestDto.isOrganiser())
                .password(bCryptPasswordEncoder.encode(userRequestDto.getPassword()))
                .profilePhotoUrl(userRequestDto.getProfilePhotoUrl())
                .email(userRequestDto.getEmail())
                .build();

        try
        {
            User savedUser = userRepository.save(user);

            System.out.println(userRequestDto.isOrganiser() + " - " + savedUser.isOrganiser());
            UserResponseDto responseDto= UserResponseDto.builder()
                    .id(savedUser.getId())
                    .firstName(savedUser.getFirstName())
                    .lastName(savedUser.getLastName())
                    .isOrganiser(savedUser.isOrganiser())
                    .profilePhotoUrl(savedUser.getProfilePhotoUrl())
                    .email(savedUser.getEmail())
                    .build();

            ApiResponseDTO<User> apiResponseDTO= ApiResponseDTO.<User>builder()
                    .success(true)
                    .message("User registered successfully")
                    .data(savedUser)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponseDTO);


        }catch (Exception e){
            ApiResponseDTO<String> apiResponseDTO= ApiResponseDTO.<String>builder()
                    .success(true)
                    .message("something wents wrong")
                    .build();
            return ResponseEntity.status(403).body(apiResponseDTO);

        }


    }


    @Override
    public ResponseEntity<ApiResponseDTO<?>> getUserByEmailId(UserRequestDto emailPassword) {
        User user = userRepository.findByEmail(emailPassword.getEmail());

        if (user == null || !user.getPassword().equals(emailPassword.getPassword())) {
            ApiResponseDTO<String> apiResponseDTO = ApiResponseDTO.<String>builder()
                    .success(true)
                    .message("User Not found")
                    .build();
            return ResponseEntity.status(403).body(apiResponseDTO);
        }

        UserResponseDto responseDto = UserResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isOrganiser(user.isOrganiser())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .email(user.getEmail())
                .build();
        ApiResponseDTO<User> apiResponseDTO = ApiResponseDTO.<User>builder()
                .success(true)
                .message("Success")
                .data(user)
                .build();
        return ResponseEntity.ok().body(apiResponseDTO);
    }


    @Override
    public ResponseEntity<ApiResponseDTO<?>> updateUser(Long id, UserRequestDto userRequestDto, String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        if (userRepository.findById(id).isPresent()) {
            User newUser = userRepository.findById(id).get();
            if (!jwtService.isValidToken(token, newUser.getEmail())) {
                ApiResponseDTO<String> apiResponseDTO = ApiResponseDTO.<String>builder()
                        .success(true)
                        .message("Not Authentic Person")
                        .build();
                return ResponseEntity.status(403).body(apiResponseDTO);
            }

            if (userRequestDto.getFirstName() != null) newUser.setFirstName(userRequestDto.getFirstName());
            if (userRequestDto.getLastName() != null) newUser.setLastName(userRequestDto.getLastName());
            if (userRequestDto.getProfilePhotoUrl() != null)
                newUser.setProfilePhotoUrl(userRequestDto.getProfilePhotoUrl());
            if (userRequestDto.getPassword() != null) {
                newUser.setPassword(bCryptPasswordEncoder.encode(userRequestDto.getPassword()));
            }
            userRepository.save(newUser);
            ApiResponseDTO<User> apiResponseDTO = ApiResponseDTO.<User>builder()
                    .success(true)
                    .message("Update sucessfuly")
                    .data(newUser)
                    .build();
            return ResponseEntity.ok(apiResponseDTO);

        }
        ApiResponseDTO<String> wrongResponse = ApiResponseDTO.<String>builder()
                .success(false)
                .message("Database error while updating user")
                .build();

        return ResponseEntity.status(500).body(wrongResponse);

    }

    @Override
    public ResponseEntity<ApiResponseDTO<?>> deleteUser(Long id, String authHeader) {

        if (!userRepository.existsById(id)) {

            ApiResponseDTO<String> wrongResponse = ApiResponseDTO.<String>builder()
                    .success(false)
                    .message("Database error while deleting user")
                    .build();

            return ResponseEntity.status(500).body(wrongResponse);
        }
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        User newUser = userRepository.findById(id).get();
        if (!jwtService.isValidToken(token, newUser.getEmail())) {
            ApiResponseDTO<String> apiResponseDTO = ApiResponseDTO.<String>builder()
                    .success(true)
                    .message("Login First")
                    .build();
            return ResponseEntity.status(403).body(apiResponseDTO);
        }


        userRepository.deleteById(id);

        ApiResponseDTO<String> apiResponseDTO = ApiResponseDTO.<String>builder()
                .success(true)
                .message("User Account is Delete ")
                .build();

        return ResponseEntity.ok(apiResponseDTO);

    }


    @Override
    public ResponseEntity<ApiResponseDTO<?>> verifyUser(@RequestBody UserRequestDto loginRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user=userRepository.findByEmail(loginRequest.getEmail());
            String JwtToken=jwtService.generateToken(user);
            ApiResponseDTO<User> response = ApiResponseDTO.<User>builder()
                    .success(true)
                    .message("Login Success")
                    .data(user)
                    .token(JwtToken)
                    .build();

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            ApiResponseDTO<String> errorResponse = ApiResponseDTO.<String>builder()
                    .success(false)
                    .message("Invalid email or password")
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<ApiResponseDTO<?>> getMyBookings() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String email = userDetails.getUsername();

            return ticketService.getTicketByEmail(email);
        } catch (Exception e) {
            e.printStackTrace(); // Helpful for debugging
            ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                    .success(false)
                    .message("something wents wrong")
                    .data(null)
                    .build();
            return ResponseEntity.status(503).body(response);
        }
    }

    @Override
    public ResponseEntity<ApiResponseDTO<?>> getMyTicket(long id) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponseDTO<?>>getAllTicketByEventId(long id,String authHeader){
         try
         {
             Optional<Event> event=eventRepository.findById(id);
             if(event.isEmpty()){
                 ApiResponseDTO<String> apiResponseDTO = ApiResponseDTO.<String>builder()
                         .success(true)
                         .message("Event Not Found")
                         .build();
                 return ResponseEntity.status(404).body(apiResponseDTO);
             }

             String createEmail=event.get().getCreatedBy().getEmail();
             String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
             if (!jwtService.isValidToken(token,createEmail)){
                 ApiResponseDTO<String> apiResponseDTO = ApiResponseDTO.<String>builder()
                         .success(false)
                         .message("Not Authorized Person")
                         .build();
                 return ResponseEntity.status(403).body(apiResponseDTO);
             }

             List<Ticket> tickets=ticketRepository.findByEventId(id);
             ApiResponseDTO<List<Ticket>> apiResponseDTO = ApiResponseDTO.<List<Ticket>>builder()
                     .success(true)
                     .message("ALl tickets are fetched")
                     .data(tickets)
                     .build();
             return ResponseEntity.status(200).body(apiResponseDTO);

         }catch (Exception e)
         {
             e.printStackTrace();
             ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                     .success(false)
                     .message(e.getMessage())
                     .data(null)
                     .build();
             return ResponseEntity.status(503).body(response);
         }
    }



}
