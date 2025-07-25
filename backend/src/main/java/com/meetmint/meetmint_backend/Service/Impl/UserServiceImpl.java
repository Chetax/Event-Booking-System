package com.meetmint.meetmint_backend.Service.Impl;

import com.meetmint.meetmint_backend.CustomUserDetails;
import com.meetmint.meetmint_backend.Dto.UserRequestDto;
import com.meetmint.meetmint_backend.Dto.UserResponseDto;
import com.meetmint.meetmint_backend.Dto.ApiResponseDTO;
import com.meetmint.meetmint_backend.Model.User;
import com.meetmint.meetmint_backend.Repository.UserRepository;
import com.meetmint.meetmint_backend.Service.JwtService;
import com.meetmint.meetmint_backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public ResponseEntity<ApiResponseDTO<?>> createUser(UserRequestDto userRequestDto) {

        User user = User.builder()
                .firstName(userRequestDto.getFirstName())
                .lastName(userRequestDto.getLastName())
                .isOrganiser(userRequestDto.isOrganiser())
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
                    .message("User Created Success")
                    .data(savedUser)
                    .build();
            return ResponseEntity.ok().body(apiResponseDTO);


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
          ApiResponseDTO<String> apiResponseDTO= ApiResponseDTO.<String>builder()
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
      ApiResponseDTO<User> apiResponseDTO= ApiResponseDTO.<User>builder()
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
                        .message("Login First")
                        .build();
                return ResponseEntity.status(403).body(apiResponseDTO);
            }

            if (userRequestDto.getFirstName() != null) newUser.setFirstName(userRequestDto.getFirstName());
            if (userRequestDto.getLastName() != null) newUser.setLastName(userRequestDto.getLastName());
            if (userRequestDto.getProfilePhotoUrl() != null) newUser.setProfilePhotoUrl(userRequestDto.getProfilePhotoUrl());
            if (userRequestDto.getEmail() != null) newUser.setEmail(userRequestDto.getEmail());
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
    public ResponseEntity<ApiResponseDTO<?>> deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            ApiResponseDTO<String> wrongResponse= ApiResponseDTO.<String>builder()
                    .success(false)
                    .message("Database error while deleting user")
                    .build();

            return ResponseEntity.status(500).body(wrongResponse);
        }

          userRepository.deleteById(id);

        ApiResponseDTO<String> apiResponseDTO= ApiResponseDTO.<String>builder()
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


}
