package br.com.conectapet.security;

import br.com.conectapet.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
        HttpServletRequest  req,
        HttpServletResponse res,
        FilterChain         chain
    ) throws ServletException, IOException {

        String token = extractToken(req);

        if (StringUtils.hasText(token) && jwtUtil.isValid(token)) {
            String email = jwtUtil.getEmail(token);
            long tokenVersion = jwtUtil.getTokenVersion(token);

            boolean versionValid = userRepository.findByEmail(email)
                .map(u -> {
                    long dbVersion = u.getTokenVersion() != null ? u.getTokenVersion() : 1L;
                    return dbVersion == tokenVersion;
                })
                .orElse(false);

            if (versionValid) {
                var userDetails = userDetailsService.loadUserByUsername(email);
                var auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.debug("Token rejeitado: versão inválida para {}", email);
            }
        }

        chain.doFilter(req, res);
    }

    private String extractToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
