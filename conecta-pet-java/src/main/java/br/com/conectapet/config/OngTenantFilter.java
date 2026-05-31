package br.com.conectapet.config;

import br.com.conectapet.model.Ong;
import br.com.conectapet.model.OngMembro;
import br.com.conectapet.model.User;
import br.com.conectapet.repository.OngMembroRepository;
import br.com.conectapet.repository.OngRepository;
import br.com.conectapet.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OngTenantFilter extends OncePerRequestFilter {

    private final UserRepository      userRepository;
    private final OngRepository       ongRepository;
    private final OngMembroRepository membroRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest req, HttpServletResponse res, FilterChain chain
    ) throws ServletException, IOException {
        try {
            String header = req.getHeader("X-Ong-Id");
            if (header != null && !header.isBlank()) {
                resolveAndSetTenant(header);
            }
            chain.doFilter(req, res);
        } finally {
            TenantContext.clear();
        }
    }

    private void resolveAndSetTenant(String ongIdStr) {
        try {
            Long ongId = Long.parseLong(ongIdStr);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) return;

            User user = userRepository.findByEmail(auth.getName()).orElse(null);
            if (user == null) return;

            Ong ong = ongRepository.findById(ongId).orElse(null);
            if (ong == null) return;

            OngMembro membro = membroRepository.findByOngAndUser(ong, user).orElse(null);
            if (membro == null) return;

            boolean isOngAdmin = membro.getRole() == OngMembro.OngMembroRole.ONG_ADMIN;
            TenantContext.set(ongId, isOngAdmin);

            if (isOngAdmin) {
                List<GrantedAuthority> authorities = new ArrayList<>(auth.getAuthorities());
                authorities.add(new SimpleGrantedAuthority("ROLE_ONG_ADMIN"));
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), authorities)
                );
            }
        } catch (NumberFormatException e) {
            log.debug("X-Ong-Id inválido: {}", ongIdStr);
        }
    }
}
