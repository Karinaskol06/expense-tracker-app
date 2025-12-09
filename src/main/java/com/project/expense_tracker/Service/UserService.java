package com.project.expense_tracker.Service;

import com.project.expense_tracker.DTO.AuthDTO.ChangePasswordDTO;
import com.project.expense_tracker.DTO.AuthDTO.RegisterRequestDTO;
import com.project.expense_tracker.DTO.UserDTO.UpdateUserDTO;
import com.project.expense_tracker.DTO.UserDTO.UserDTO;
import com.project.expense_tracker.Entity.Roles;
import com.project.expense_tracker.Entity.User;
import com.project.expense_tracker.Exceptions.ResourceNotFoundException;
import com.project.expense_tracker.Repository.RolesRepository;
import com.project.expense_tracker.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;

    public boolean existsByUsername (String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail (String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public UserDTO registerUser(RegisterRequestDTO registerRequest) {

        if (existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        if (existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        Roles defaultRole = rolesRepository.findByRoleName("USER").orElseThrow();
        String hashedPass = passwordEncoder.encode(registerRequest.getPassword());

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(hashedPass)
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .role(defaultRole)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUser(Long userId, UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found" + userId));

        if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(updateUserDTO.getUsername())) {
                throw new IllegalArgumentException("Username is already in use");
            }
            user.setUsername(updateUserDTO.getUsername());
        }

        if (updateUserDTO.getEmail() != null && !updateUserDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateUserDTO.getEmail())) {
                throw new IllegalArgumentException("Email is already in use");
            }
            user.setEmail(updateUserDTO.getEmail());
        }

        if (updateUserDTO.getFirstName() != null) {
            user.setFirstName(updateUserDTO.getFirstName());
        }
        if (updateUserDTO.getLastName() != null) {
            user.setLastName(updateUserDTO.getLastName());
        }

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found" + userId));

        if (changePasswordDTO.getNewPassword() != null) {
            user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        }
        userRepository.save(user);
    }

    @Transactional
    public UserDTO changeUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.getRole().setRoleName(newRole);

        return convertToDTO(userRepository.save(user));
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserDTOById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found" + id));
        return convertToDTO(user);
    }

    public Long getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found" + username));
        return user.getId();
    }

    public User getUserEntityById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found" + id));
        return user;
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found" + id);
        } else {
            userRepository.deleteById(id);
        }
    }

    public UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(List.of(user.getRole().getRoleName()))
                .build();
    }
}
