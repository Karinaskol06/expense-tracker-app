package com.project.expense_tracker.DTO.AuthDTO;

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
public class ChangePasswordDTO {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 5, max = 100, message = "Password must be between 5 and 100 characters")
    private String newPassword;

    @NotBlank(message = "Confirm new password")
    private String confirmPassword;
}
