package com.project.expense_tracker.Security;

import com.project.expense_tracker.Exceptions.UnauthorizedException;
import com.project.expense_tracker.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserService userService;

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        String username = auth.getName();
        return userService.getUserIdByUsername(username);
    }
}
