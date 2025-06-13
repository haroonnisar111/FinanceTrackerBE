package com.budgetbuddy.personal_finance_tracker.controller;
import com.budgetbuddy.personal_finance_tracker.dto.ApiResponse;
import com.budgetbuddy.personal_finance_tracker.dto.LoginRequest;
import com.budgetbuddy.personal_finance_tracker.dto.LoginResponse;
import com.budgetbuddy.personal_finance_tracker.dto.UserRequest;
import com.budgetbuddy.personal_finance_tracker.dto.UserResponse;
import com.budgetbuddy.personal_finance_tracker.entity.User;
import com.budgetbuddy.personal_finance_tracker.service.UserService;
import com.budgetbuddy.personal_finance_tracker.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRequest userRequest) {
        try {
            User user = mapToEntity(userRequest);
            User createdUser = userService.createUser(user);
            UserResponse response = mapToResponse(createdUser);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error registering user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to register user"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            // Update last login time
            User user = (User) authentication.getPrincipal();
            userService.updateLastLogin(user.getId());

            LoginResponse response = LoginResponse.builder()
                    .accessToken(jwt)
                    .tokenType("Bearer")
                    .user(mapToResponse(user))
                    .build();

            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            log.error("Error during authentication", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid username/email or password"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    private User mapToEntity(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        return user;
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
