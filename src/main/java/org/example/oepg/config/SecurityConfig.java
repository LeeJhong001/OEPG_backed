package org.example.oepg.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeRequests(auth -> auth
                        .antMatchers("/api/auth/**").permitAll()
                        .antMatchers("/api/public/**").permitAll()
                        .antMatchers("/api/test/public").permitAll()
                        .antMatchers("/api/roles/**").permitAll()
                        // 题库管理权限控制
                        .antMatchers(HttpMethod.GET, "/api/question-categories/**").hasAnyRole("TEACHER", "ADMIN")
                        .antMatchers(HttpMethod.POST, "/api/question-categories/**").hasAnyRole("TEACHER", "ADMIN")
                        .antMatchers(HttpMethod.PUT, "/api/question-categories/**").hasAnyRole("TEACHER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/api/question-categories/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/api/questions/**").hasAnyRole("TEACHER", "ADMIN")
                        .antMatchers(HttpMethod.POST, "/api/questions/**").hasAnyRole("TEACHER", "ADMIN")
                        .antMatchers(HttpMethod.PUT, "/api/questions/**").hasAnyRole("TEACHER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/api/questions/**").hasAnyRole("TEACHER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}