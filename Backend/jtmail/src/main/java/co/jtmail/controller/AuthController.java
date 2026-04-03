package co.jtmail.controller;

import co.jtmail.dto.request.LoginRequest;
import co.jtmail.dto.response.UserResponse;
import co.jtmail.mapper.UserMapper;
import co.jtmail.model.User;
import co.jtmail.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        // Si las credenciales son incorrectas Spring lanza BadCredentialsException
        // que el GlobalExceptionHandler captura y devuelve 401
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(HttpServletRequest request) {
        // Extrae el email del token que viene en el header Authorization
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtService.extractEmail(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        User user = (User) userDetails;

        return ResponseEntity.ok(UserMapper.toResponse(user));
    }
}