package com.MindMitra.Rolebased.Config;

import com.MindMitra.Rolebased.Entity.Doctor;
import com.MindMitra.Rolebased.Entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final String role;
    private final Object entity; // Store original entity for later use (User or Doctor)

    // Constructor for User entity
    public CustomUserDetails(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole().name();
        this.entity = user;
    }

    // Constructor for Doctor entity
    public CustomUserDetails(Doctor doctor) {
        this.username = doctor.getDoctorName();
        this.password = doctor.getPassword();
        this.role = doctor.getRole().name();
        this.entity = doctor;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Return the original entity for service layer usage
    public Object getEntity() {
        return entity;
    }
}
