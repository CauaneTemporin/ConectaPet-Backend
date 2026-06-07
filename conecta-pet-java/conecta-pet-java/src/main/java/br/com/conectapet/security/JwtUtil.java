package br.com.conectapet.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        byte[] decoded = Base64.getDecoder().decode(secret);
        this.key         = Keys.hmacShaKeyFor(decoded);
        this.expirationMs = expirationMs;
    }

    /** Gera token JWT com o e-mail como subject e a versão do token */
    public String generate(String email, long tokenVersion) {
        return Jwts.builder()
            .subject(email)
            .claim("tv", tokenVersion)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(key)
            .compact();
    }

    /** Extrai o e-mail (subject) do token */
    public String getEmail(String token) {
        return claims(token).getSubject();
    }

    /** Extrai a versão do token (claim "tv") */
    public long getTokenVersion(String token) {
        Object tv = claims(token).get("tv");
        if (tv instanceof Number n) return n.longValue();
        return 1L;
    }

    /** Valida o token — retorna true se válido */
    public boolean isValid(String token) {
        try {
            claims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    private Claims claims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
