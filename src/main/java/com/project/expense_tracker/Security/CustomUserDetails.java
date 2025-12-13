package com.project.expense_tracker.Security;

import com.project.expense_tracker.Entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

//adapter for user entity (to interface that spring security understands)
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getUserId() { return user.getId(); }

    public String getEmail() { return user.getEmail(); }

    public String getRole() {
         String role = user.getRole().getRoleName();
         return role.startsWith("ROLE_") ? role : "ROLE_" + role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = user.getRole().getRoleName();
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    //if returns false, block the user from logging in because the account is locked
    @Override
    public boolean isAccountNonLocked() { return true; }

    //true if the user's credentials (password) are still valid
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
