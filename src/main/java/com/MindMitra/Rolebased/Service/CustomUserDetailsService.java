package com.MindMitra.Rolebased.Service;

import com.MindMitra.Rolebased.Config.CustomUserDetails;
import com.MindMitra.Rolebased.Entity.User;
import com.MindMitra.Rolebased.Entity.Doctor;
import com.MindMitra.Rolebased.Repository.UsersRepo;
import com.MindMitra.Rolebased.Repository.docRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepo usersRepo;
    private final docRepo doctorRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Try from users table
        User user = usersRepo.findByUsername(username).orElse(null);
        if (user != null) {
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.singleton(authority)
            );
        }

        // Try from doctors table
        Doctor doctor = doctorRepo.findByDoctorName(username).orElse(null);
        if (doctor != null) {
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_DOCTOR");
            return new org.springframework.security.core.userdetails.User(
                    doctor.getDoctorName(),
                    doctor.getPassword(),
                    Collections.singleton(authority)
            );
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
