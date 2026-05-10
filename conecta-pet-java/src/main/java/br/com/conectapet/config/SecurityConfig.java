package br.com.conectapet.config;

import br.com.conectapet.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ── Públicas ──────────────────────────────────────────
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/animals/**").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/shelters").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/donations").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/donations/stats").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
                // ── Admin ─────────────────────────────────────────────
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/animals").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/animals/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/api/animals/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/animals/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/shelters").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/contact").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/api/contact/*/read").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/volunteers").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/godparents").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/api/volunteers/*/approve").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/api/volunteers/*/reject").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/donations").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/api/adoptions/*/review").hasRole("ADMIN")
                // ── Autenticadas ──────────────────────────────────────
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
