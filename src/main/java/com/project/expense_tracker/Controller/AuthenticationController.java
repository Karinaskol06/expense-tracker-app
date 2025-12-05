package com.project.expense_tracker.Controller;

import com.project.expense_tracker.DTO.LoginRequestDTO;
import com.project.expense_tracker.DTO.LoginResponseDTO;
import com.project.expense_tracker.DTO.RegisterRequestDTO;
import com.project.expense_tracker.DTO.UserDTO;
import com.project.expense_tracker.Security.CustomUserDetails;
import com.project.expense_tracker.Service.JwtService;
import com.project.expense_tracker.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateToken(userDetails);

        LoginResponseDTO response = LoginResponseDTO.builder()
                .token(jwt)
                .type("Bearer")
                .id(userDetails.getUserId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(null);
        }
        UserDTO registeredUser = userService.registerUser(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String username = jwtService.extractUsername(jwt);
            return ResponseEntity.ok(username != null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(false);
        }
    }

}
