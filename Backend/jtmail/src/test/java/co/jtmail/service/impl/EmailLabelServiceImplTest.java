package co.jtmail.service.impl;

import co.jtmail.dto.response.EmailLabelResponse;
import co.jtmail.exception.ConflictException;
import co.jtmail.exception.ResourceNotFoundException;
import co.jtmail.model.*;
import co.jtmail.model.enums.EmailStatus;
import co.jtmail.repository.EmailLabelRepository;
import co.jtmail.repository.EmailRepository;
import co.jtmail.repository.LabelRepository;
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
class EmailLabelServiceImplTest {

    @Mock private EmailLabelRepository emailLabelRepository;
    @Mock private EmailRepository emailRepository;
    @Mock private LabelRepository labelRepository;

    @InjectMocks
    private EmailLabelServiceImpl emailLabelService;

    private User mockUser;
    private Email mockEmail;
    private Label mockLabel;
    private EmailLabel mockEmailLabel;
    private UUID emailId;
    private UUID labelId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        emailId = UUID.randomUUID();
        labelId = UUID.randomUUID();
        userId  = UUID.randomUUID();

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

        MailThread mockThread = MailThread.builder()
                .idThread(UUID.randomUUID())
                .createdAt(Instant.now())
                .lastEmailAt(Instant.now())
                .build();

        mockEmail = Email.builder()
                .idEmail(emailId)
                .thread(mockThread)
                .sender(mockUser)
                .subject("Asunto test")
                .body("Cuerpo test")
                .status(EmailStatus.SENT)
                .sentAt(Instant.now())
                .createdAt(Instant.now())
                .build();

        mockLabel = Label.builder()
                .idLabel(labelId)
                .name("Trabajo")
                .color("#FF5733")
                .isSystem(false)
                .user(mockUser)
                .createdAt(Instant.now())
                .build();

        mockEmailLabel = EmailLabel.builder()
                .email(mockEmail)
                .label(mockLabel)
                .build();
    }

    // ─── getLabelsByEmail ──────────────────────────────────────

    @Test
    void getLabelsByEmail_retornaLabelsDelCorreo() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.of(mockEmail));
        when(emailLabelRepository.findByEmail(mockEmail)).thenReturn(List.of(mockEmailLabel));

        List<EmailLabelResponse> result = emailLabelService.getLabelsByEmail(emailId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLabelName()).isEqualTo("Trabajo");
        assertThat(result.get(0).getEmailId()).isEqualTo(emailId);
    }

    @Test
    void getLabelsByEmail_retornaListaVaciaSiNoHayLabels() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.of(mockEmail));
        when(emailLabelRepository.findByEmail(mockEmail)).thenReturn(List.of());

        assertThat(emailLabelService.getLabelsByEmail(emailId)).isEmpty();
    }

    @Test
    void getLabelsByEmail_lanzaExcepcionSiEmailNoExiste() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailLabelService.getLabelsByEmail(emailId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── addLabelToEmail ───────────────────────────────────────

    @Test
    void addLabelToEmail_asignaLabelCorrectamente() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.of(mockEmail));
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(mockLabel));
        when(emailLabelRepository.existsByEmailAndLabel(mockEmail, mockLabel)).thenReturn(false);
        when(emailLabelRepository.save(any(EmailLabel.class))).thenReturn(mockEmailLabel);

        EmailLabelResponse result = emailLabelService.addLabelToEmail(emailId, labelId);

        assertThat(result).isNotNull();
        assertThat(result.getLabelId()).isEqualTo(labelId);
        verify(emailLabelRepository).save(any(EmailLabel.class));
    }

    @Test
    void addLabelToEmail_lanzaConflictSiLabelYaAsignado() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.of(mockEmail));
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(mockLabel));
        when(emailLabelRepository.existsByEmailAndLabel(mockEmail, mockLabel)).thenReturn(true);

        assertThatThrownBy(() -> emailLabelService.addLabelToEmail(emailId, labelId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("label");

        verify(emailLabelRepository, never()).save(any());
    }

    @Test
    void addLabelToEmail_lanzaExcepcionSiEmailNoExiste() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailLabelService.addLabelToEmail(emailId, labelId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(emailLabelRepository, never()).save(any());
    }

    @Test
    void addLabelToEmail_lanzaExcepcionSiLabelNoExiste() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.of(mockEmail));
        when(labelRepository.findById(labelId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailLabelService.addLabelToEmail(emailId, labelId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(emailLabelRepository, never()).save(any());
    }

    // ─── removeLabelFromEmail ──────────────────────────────────

    @Test
    void removeLabelFromEmail_eliminaLabelCorrectamente() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.of(mockEmail));
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(mockLabel));
        when(emailLabelRepository.existsByEmailAndLabel(mockEmail, mockLabel)).thenReturn(true);

        emailLabelService.removeLabelFromEmail(emailId, labelId);

        verify(emailLabelRepository).deleteByEmailAndLabel(mockEmail, mockLabel);
    }

    @Test
    void removeLabelFromEmail_lanzaExcepcionSiRelacionNoExiste() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.of(mockEmail));
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(mockLabel));
        when(emailLabelRepository.existsByEmailAndLabel(mockEmail, mockLabel)).thenReturn(false);

        assertThatThrownBy(() -> emailLabelService.removeLabelFromEmail(emailId, labelId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(emailLabelRepository, never()).deleteByEmailAndLabel(any(), any());
    }

    @Test
    void removeLabelFromEmail_lanzaExcepcionSiEmailNoExiste() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailLabelService.removeLabelFromEmail(emailId, labelId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(emailLabelRepository, never()).deleteByEmailAndLabel(any(), any());
    }
}