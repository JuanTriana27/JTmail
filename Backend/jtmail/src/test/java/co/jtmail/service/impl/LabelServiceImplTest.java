package co.jtmail.service.impl;

import co.jtmail.dto.request.CreateLabelRequest;
import co.jtmail.dto.response.LabelResponse;
import co.jtmail.exception.ConflictException;
import co.jtmail.exception.ResourceNotFoundException;
import co.jtmail.model.Label;
import co.jtmail.model.User;
import co.jtmail.repository.LabelRepository;
import co.jtmail.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabelServiceImplTest {

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LabelServiceImpl labelService;

    private User mockUser;
    private Label mockLabel;
    private Label mockSystemLabel;
    private UUID userId;
    private UUID labelId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        labelId = UUID.randomUUID();

        mockUser = User.builder()
                .idUser(userId)
                .email("juan@test.com")
                .fullName("Juan Torres")
                .passwordHash("$2a$10$hash")
                .isActive(true)
                .unreadCount(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        mockLabel = Label.builder()
                .idLabel(labelId)
                .name("Trabajo")
                .color("#FF5733")
                .isSystem(false)
                .user(mockUser)
                .createdAt(Instant.now())
                .build();

        // Label de sistema para probar restricciones
        mockSystemLabel = Label.builder()
                .idLabel(UUID.randomUUID())
                .name("INBOX")
                .color(null)
                .isSystem(true)
                .user(mockUser)
                .createdAt(Instant.now())
                .build();
    }

    // ─── getLabelsByUser ───────────────────────────────────────

    @Test
    void getLabelsByUser_retornaLabelsDelUsuario() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(labelRepository.findByUser(mockUser)).thenReturn(List.of(mockLabel));

        List<LabelResponse> result = labelService.getLabelsByUser(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Trabajo");
    }

    @Test
    void getLabelsByUser_lanzaExcepcionSiUsuarioNoExiste() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> labelService.getLabelsByUser(userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getLabelById ──────────────────────────────────────────

    @Test
    void getLabelById_retornaLabelCuandoExiste() {
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(mockLabel));

        LabelResponse result = labelService.getLabelById(labelId);

        assertThat(result.getIdLabel()).isEqualTo(labelId);
        assertThat(result.getName()).isEqualTo("Trabajo");
    }

    @Test
    void getLabelById_lanzaExcepcionCuandoNoExiste() {
        when(labelRepository.findById(labelId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> labelService.getLabelById(labelId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── createLabel ───────────────────────────────────────────

    @Test
    void createLabel_creaYRetornaLabel() {
        CreateLabelRequest request = CreateLabelRequest.builder()
                .name("Personal")
                .color("#33FF57")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(labelRepository.existsByUserAndName(mockUser, request.getName())).thenReturn(false);
        when(labelRepository.save(any(Label.class))).thenReturn(mockLabel);

        LabelResponse result = labelService.createLabel(userId, request);

        assertThat(result).isNotNull();
        verify(labelRepository).save(any(Label.class));
    }

    @Test
    void createLabel_lanzaConflictSiNombreYaExiste() {
        CreateLabelRequest request = CreateLabelRequest.builder()
                .name("Trabajo")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(labelRepository.existsByUserAndName(mockUser, request.getName())).thenReturn(true);

        assertThatThrownBy(() -> labelService.createLabel(userId, request))
                .isInstanceOf(ConflictException.class);

        verify(labelRepository, never()).save(any());
    }

    // ─── updateLabel ───────────────────────────────────────────

    @Test
    void updateLabel_actualizaYRetornaLabel() {
        CreateLabelRequest request = CreateLabelRequest.builder()
                .name("Trabajo Actualizado")
                .color("#0000FF")
                .build();

        when(labelRepository.findById(labelId)).thenReturn(Optional.of(mockLabel));
        when(labelRepository.save(any(Label.class))).thenReturn(mockLabel);

        LabelResponse result = labelService.updateLabel(labelId, request);

        assertThat(result).isNotNull();
        verify(labelRepository).save(any(Label.class));
    }

    @Test
    void updateLabel_lanzaConflictSiEsLabelDeSistema() {
        CreateLabelRequest request = CreateLabelRequest.builder()
                .name("Modificado")
                .build();

        when(labelRepository.findById(mockSystemLabel.getIdLabel()))
                .thenReturn(Optional.of(mockSystemLabel));

        assertThatThrownBy(() -> labelService.updateLabel(mockSystemLabel.getIdLabel(), request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("sistema");

        verify(labelRepository, never()).save(any());
    }

    // ─── deleteLabel ───────────────────────────────────────────

    @Test
    void deleteLabel_eliminaCuandoExiste() {
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(mockLabel));

        labelService.deleteLabel(labelId);

        verify(labelRepository).deleteById(labelId);
    }

    @Test
    void deleteLabel_lanzaExcepcionCuandoNoExiste() {
        when(labelRepository.findById(labelId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> labelService.deleteLabel(labelId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(labelRepository, never()).deleteById(any());
    }

    @Test
    void deleteLabel_lanzaConflictSiEsLabelDeSistema() {
        when(labelRepository.findById(mockSystemLabel.getIdLabel()))
                .thenReturn(Optional.of(mockSystemLabel));

        assertThatThrownBy(() -> labelService.deleteLabel(mockSystemLabel.getIdLabel()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("sistema");

        verify(labelRepository, never()).deleteById(any());
    }
}