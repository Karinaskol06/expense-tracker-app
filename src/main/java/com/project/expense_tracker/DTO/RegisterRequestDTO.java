package com.project.expense_tracker.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {

    @NotBlank (message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank (message = "Email is required")
    @Email (message = "Email should be valid")
    private String email;

    @NotBlank (message = "Password is required")
    @Size(min = 5, max = 100, message = "Password must be between 5 and 100 characters")
    private String password;

    private String firstName;
    private String lastName;
}
