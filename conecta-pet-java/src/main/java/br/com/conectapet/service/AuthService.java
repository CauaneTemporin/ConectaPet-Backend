package br.com.conectapet.service;

import br.com.conectapet.dto.*;
import br.com.conectapet.model.User;
import br.com.conectapet.repository.UserRepository;
import br.com.conectapet.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil         jwtUtil;

    /** Cadastra novo usuário e retorna token JWT */
    @Transactional
    public AuthDTOs.AuthResponse register(AuthDTOs.RegisterRequest req) {
        if (userRepository.existsByEmail(req.email().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "E-mail já cadastrado.");
        }

        var user = User.builder()
            .name(req.name().trim())
            .email(req.email().toLowerCase().trim())
            .password(passwordEncoder.encode(req.password()))
            .city(req.city() != null ? req.city().trim() : "")
            .phone(req.phone() != null && !req.phone().isBlank() ? req.phone().trim() : null)
            .role(User.Role.USER)
            .build();

        userRepository.save(user);
        log.info("Novo usuário registrado: {}", user.getEmail());

        String token = jwtUtil.generate(user.getEmail(), user.getTokenVersion() != null ? user.getTokenVersion() : 1L);
        return new AuthDTOs.AuthResponse(token, UserDTOs.UserResponse.from(user));
    }

    /** Autentica usuário e retorna token JWT */
    public AuthDTOs.AuthResponse login(AuthDTOs.LoginRequest req) {
        var user = userRepository.findByEmail(req.email().toLowerCase())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "E-mail ou senha incorretos."));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "E-mail ou senha incorretos.");
        }

        log.info("Login: {}", user.getEmail());
        String token = jwtUtil.generate(user.getEmail(), user.getTokenVersion() != null ? user.getTokenVersion() : 1L);
        return new AuthDTOs.AuthResponse(token, UserDTOs.UserResponse.from(user));
    }

    /** Retorna o usuário autenticado */
    public UserDTOs.UserResponse getProfile(String email) {
        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        return UserDTOs.UserResponse.from(user);
    }

    /** Atualiza nome, cidade e bio */
    @Transactional
    public UserDTOs.UserResponse updateProfile(String email, AuthDTOs.UpdateProfileRequest req) {
        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        if (req.name()  != null && !req.name().isBlank()) user.setName(req.name().trim());
        if (req.city()  != null)                           user.setCity(req.city().trim());
        if (req.bio()   != null)                           user.setBio(req.bio().trim());
        if (req.phone() != null)                           user.setPhone(req.phone().isBlank() ? null : req.phone().trim());

        return UserDTOs.UserResponse.from(userRepository.save(user));
    }

    /** Busca usuário por e-mail (uso interno) */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }
}
