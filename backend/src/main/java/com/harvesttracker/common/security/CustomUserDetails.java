package com.harvesttracker.common.security;

import com.harvesttracker.features.auth.domain.Permission;
import com.harvesttracker.features.auth.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;
        this.authorities = new HashSet<>();

        // Add Role authority (prefixed with ROLE_)
        if (user.getRole() != null && user.getRole().getCode() != null) {
            String roleCode = user.getRole().getCode().toUpperCase();
            this.authorities.add(new SimpleGrantedAuthority("ROLE_" + roleCode));

            // Add Permissions authorities
            if (user.getRole().getPermissions() != null) {
                for (Permission permission : user.getRole().getPermissions()) {
                    if (permission.isActive() && permission.getDeletedAt() == null) {
                        this.authorities.add(new SimpleGrantedAuthority(permission.getCode()));
                    }
                }
            }
        }
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getDeletedAt() == null;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"SUSPENDED".equalsIgnoreCase(user.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive() && "ACTIVE".equalsIgnoreCase(user.getStatus()) && user.getDeletedAt() == null;
    }
}
