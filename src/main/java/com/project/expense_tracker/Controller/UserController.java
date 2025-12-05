package com.project.expense_tracker.Controller;

import com.project.expense_tracker.DTO.ChangePasswordDTO;
import com.project.expense_tracker.DTO.UpdateUserDTO;
import com.project.expense_tracker.DTO.UserDTO;
import com.project.expense_tracker.Security.CustomUserDetails;
import com.project.expense_tracker.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    //get current user profile
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getCurrentUserProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserDTO user = userService.getUserDTOById(customUserDetails.getUserId());
        return ResponseEntity.ok(user);
    }

    //update profile
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateUserProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        UserDTO updatedUser = userService.updateUser(
                customUserDetails.getUserId(),
                updateUserDTO
        );

        return ResponseEntity.ok(updatedUser);
    }

    //change password
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody ChangePasswordDTO changePasswordDto) {
        userService.changePassword(customUserDetails.getUserId(), changePasswordDto);
        return ResponseEntity.noContent().build();
    }

    //get user by id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserDTOById(id);
        return ResponseEntity.ok(user);
    }

    //get all users (for admin)
    @GetMapping("/admin/all-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    //delete user by id (self-delete or admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        boolean isAdmin = customUserDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isSelfDel = customUserDetails.getUserId().equals(id);

        if (!isAdmin && !isSelfDel) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
