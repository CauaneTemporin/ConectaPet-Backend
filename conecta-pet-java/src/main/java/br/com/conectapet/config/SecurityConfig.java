package br.com.conectapet.config;

import br.com.conectapet.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final OngTenantFilter ongTenantFilter;
    private final UserDetailsService userDetailsService;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .authorizeHttpRequests(auth -> auth
                // ── Públicas ──────────────────────────────────────────
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/animals/**").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/shelters").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/donations").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/donations/stats").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
                .requestMatchers(HttpMethod.GET,   "/api/ongs").permitAll()
                .requestMatchers(HttpMethod.GET,   "/api/ongs/{id}").permitAll()
                .requestMatchers(HttpMethod.POST,  "/api/ongs").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/ongs/*/perfil").hasAnyRole("ADMIN", "ONG_ADMIN")
                // ── Admin / ONG Admin (rotas operacionais) ───────────
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "ONG_ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/animals").hasAnyRole("ADMIN", "ONG_ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/animals/**").hasAnyRole("ADMIN", "ONG_ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/api/animals/**").hasAnyRole("ADMIN", "ONG_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/animals/**").hasAnyRole("ADMIN", "ONG_ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/shelters").hasAnyRole("ADMIN", "ONG_ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/contact").hasAnyRole("ADMIN", "ONG_ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/api/contact/*/read").hasAnyRole("ADMIN", "ONG_ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/volunteers").hasAnyRole("ADMIN", "ONG_ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/api/volunteers/*/approve").hasAnyRole("ADMIN", "ONG_ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/api/volunteers/*/reject").hasAnyRole("ADMIN", "ONG_ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/donations").hasAnyRole("ADMIN", "ONG_ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/adoptions").hasAnyRole("ADMIN", "ONG_ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/api/adoptions/*/review").hasAnyRole("ADMIN", "ONG_ADMIN")
                // ── Ocorrências ───────────────────────────────────────
                .requestMatchers(HttpMethod.POST,   "/api/occurrences").permitAll()
                .requestMatchers(HttpMethod.GET,    "/api/occurrences/mine").authenticated()
                .requestMatchers(HttpMethod.GET,    "/api/occurrences").hasAnyRole("ADMIN", "ONG_ADMIN", "GESTOR_PUBLICO")
                .requestMatchers(HttpMethod.PATCH,  "/api/occurrences/*/status").hasAnyRole("ADMIN", "ONG_ADMIN", "GESTOR_PUBLICO")
                .requestMatchers(HttpMethod.DELETE, "/api/occurrences/*").hasAnyRole("ADMIN", "ONG_ADMIN", "GESTOR_PUBLICO")
                // ── Gestor Público ────────────────────────────────────
                .requestMatchers(HttpMethod.GET,   "/api/denuncias").hasAnyRole("ADMIN", "GESTOR_PUBLICO")
                .requestMatchers(HttpMethod.PATCH, "/api/denuncias/*/status").hasAnyRole("ADMIN", "GESTOR_PUBLICO")
                // ── Autenticadas ──────────────────────────────────────
                .requestMatchers(HttpMethod.POST,  "/api/godparents").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(ongTenantFilter, JwtAuthFilter.class);

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
