package co.jtmail.service.impl;

import co.jtmail.dto.request.CreateUserRequest;
import co.jtmail.dto.request.UpdateEmailRequest;
import co.jtmail.dto.request.UpdatePasswordRequest;
import co.jtmail.dto.request.UpdateUserRequest;
import co.jtmail.dto.response.UserResponse;
import co.jtmail.exception.ConflictException;
import co.jtmail.exception.ResourceNotFoundException;
import co.jtmail.model.User;
import co.jtmail.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private UUID userId;

    // Se ejecuta antes de cada test — evita repetir el setup
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        mockUser = User.builder()
                .idUser(userId)
                .email("juan@test.com")
                .fullName("Juan Torres")
                .passwordHash("$2a$10$hasheado")
                .isActive(true)
                .unreadCount(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    // ─── getAllUsers ───────────────────────────────────────────

    @Test
    void getAllUsers_retornaListaDeUsuarios() {
        when(userRepository.findAll()).thenReturn(List.of(mockUser));

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("juan@test.com");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_retornaListaVaciaSiNoHayUsuarios() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).isEmpty();
    }

    // ─── getUserById ───────────────────────────────────────────

    @Test
    void getUserById_retornaUsuarioCuandoExiste() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        UserResponse result = userService.getUserById(userId);

        assertThat(result.getIdUser()).isEqualTo(userId);
        assertThat(result.getEmail()).isEqualTo("juan@test.com");
    }

    @Test
    void getUserById_lanzaExcepcionCuandoNoExiste() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Verifica que lanza la excepción correcta, no un RuntimeException genérico
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── createUser ────────────────────────────────────────────

    @Test
    void createUser_creaYRetornaUsuario() {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("nuevo@test.com")
                .fullName("Nuevo Usuario")
                .password("password123")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse result = userService.createUser(request);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_lanzaConflictCuandoEmailDuplicado() {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("juan@test.com")
                .fullName("Otro")
                .password("password123")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("email");

        // Verifica que nunca llegó a intentar guardar
        verify(userRepository, never()).save(any());
    }

    // ─── updateUser ────────────────────────────────────────────

    @Test
    void updateUser_actualizaYRetornaUsuario() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .fullName("Juan Torres Actualizado")
                .avatarUrl("https://ejemplo.com/foto.jpg")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse result = userService.updateUser(userId, request);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_lanzaExcepcionCuandoNoExiste() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .fullName("No importa")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(ResourceNotFoundException.class);

        // Confirma que nunca intentó guardar un usuario inexistente
        verify(userRepository, never()).save(any());
    }

    // ─── updateEmail ───────────────────────────────────────────

    @Test
    void updateEmail_actualizaYRetornaUsuario() {
        UpdateEmailRequest request = UpdateEmailRequest.builder()
                .email("nuevo@test.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse result = userService.updateEmail(userId, request);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateEmail_lanzaConflictCuandoEmailYaExiste() {
        UpdateEmailRequest request = UpdateEmailRequest.builder()
                .email("ocupado@test.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.updateEmail(userId, request))
                .isInstanceOf(ConflictException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateEmail_lanzaExcepcionCuandoUsuarioNoExiste() {
        UpdateEmailRequest request = UpdateEmailRequest.builder()
                .email("nuevo@test.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateEmail(userId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

// ─── updatePassword ────────────────────────────────────────

    @Test
    void updatePassword_actualizaCorrectamente() {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("actual1234")
                .newPassword("nueva5678")
                .confirmPassword("nueva5678")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        // Simula que la contraseña actual es correcta
        when(passwordEncoder.matches(request.getCurrentPassword(), mockUser.getPasswordHash()))
                .thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        userService.updatePassword(userId, request);

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(request.getNewPassword());
    }

    @Test
    void updatePassword_lanzaConflictCuandoPasswordActualEsIncorrecta() {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("incorrecta")
                .newPassword("nueva5678")
                .confirmPassword("nueva5678")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(request.getCurrentPassword(), mockUser.getPasswordHash()))
                .thenReturn(false); // password actual no coincide

        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("incorrecta");

        verify(userRepository, never()).save(any());
    }

    @Test
    void updatePassword_lanzaConflictCuandoPasswordsNoCoinciden() {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("actual1234")
                .newPassword("nueva5678")
                .confirmPassword("diferente999") // no coincide
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(request.getCurrentPassword(), mockUser.getPasswordHash()))
                .thenReturn(true);

        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("coinciden");

        verify(userRepository, never()).save(any());
    }

    // ─── deleteUser ────────────────────────────────────────────

    @Test
    void deleteUser_eliminaCuandoExiste() {
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_lanzaExcepcionCuandoNoExiste() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).deleteById(any());
    }
}