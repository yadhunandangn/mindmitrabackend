package com.MindMitra.Rolebased.Config;

import com.MindMitra.Rolebased.Service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final CustomUserDetailsService customUserDetailsService;

    // Use the new service directly
    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }
}
