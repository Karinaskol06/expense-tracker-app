package com.project.expense_tracker.Security;

import com.project.expense_tracker.Entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

//adapter for user entity (to interface that spring security understands)
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRolesList().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    //true if the account has not expired
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

    public Long getUserId() { return user.getId(); }
}
