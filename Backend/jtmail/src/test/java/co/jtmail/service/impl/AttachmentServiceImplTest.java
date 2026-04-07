package co.jtmail.service.impl;

import co.jtmail.dto.request.CreateAttachmentRequest;
import co.jtmail.dto.response.AttachmentResponse;
import co.jtmail.exception.ResourceNotFoundException;
import co.jtmail.model.*;
import co.jtmail.model.enums.EmailStatus;
import co.jtmail.repository.AttachmentRepository;
import co.jtmail.repository.EmailRepository;
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
class AttachmentServiceImplTest {

    @Mock private AttachmentRepository attachmentRepository;
    @Mock private EmailRepository emailRepository;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private User mockUser;
    private Email mockEmail;
    private Attachment mockAttachment;
    private UUID emailId;
    private UUID attachmentId;

    @BeforeEach
    void setUp() {
        emailId      = UUID.randomUUID();
        attachmentId = UUID.randomUUID();

        mockUser = User.builder()
                .idUser(UUID.randomUUID())
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

        mockAttachment = Attachment.builder()
                .idAttachment(attachmentId)
                .email(mockEmail)
                .fileName("documento.pdf")
                .fileSize(204800L)
                .mimeType("application/pdf")
                .storageUrl("https://ejemplo.com/documento.pdf")
                .createdAt(Instant.now())
                .build();
    }

    // ─── getAttachmentsByEmail ─────────────────────────────────

    @Test
    void getAttachmentsByEmail_retornaAdjuntosDelCorreo() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.of(mockEmail));
        when(attachmentRepository.findByEmail(mockEmail)).thenReturn(List.of(mockAttachment));

        List<AttachmentResponse> result = attachmentService.getAttachmentsByEmail(emailId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFileName()).isEqualTo("documento.pdf");
        assertThat(result.get(0).getMimeType()).isEqualTo("application/pdf");
    }

    @Test
    void getAttachmentsByEmail_retornaListaVaciaSiNoHayAdjuntos() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.of(mockEmail));
        when(attachmentRepository.findByEmail(mockEmail)).thenReturn(List.of());

        assertThat(attachmentService.getAttachmentsByEmail(emailId)).isEmpty();
    }

    @Test
    void getAttachmentsByEmail_lanzaExcepcionSiEmailNoExiste() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.getAttachmentsByEmail(emailId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getAttachmentById ────────────────────────────────────

    @Test
    void getAttachmentById_retornaAdjuntoCuandoExiste() {
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(mockAttachment));

        AttachmentResponse result = attachmentService.getAttachmentById(attachmentId);

        assertThat(result.getIdAttachment()).isEqualTo(attachmentId);
        assertThat(result.getFileName()).isEqualTo("documento.pdf");
        assertThat(result.getFileSize()).isEqualTo(204800L);
    }

    @Test
    void getAttachmentById_lanzaExcepcionCuandoNoExiste() {
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.getAttachmentById(attachmentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── createAttachment ─────────────────────────────────────

    @Test
    void createAttachment_creaYRetornaAdjunto() {
        CreateAttachmentRequest request = CreateAttachmentRequest.builder()
                .emailId(emailId)
                .fileName("imagen.png")
                .fileSize(512000L)
                .mimeType("image/png")
                .storageUrl("https://ejemplo.com/imagen.png")
                .build();

        when(emailRepository.findById(emailId)).thenReturn(Optional.of(mockEmail));
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(mockAttachment);

        AttachmentResponse result = attachmentService.createAttachment(request);

        assertThat(result).isNotNull();
        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    void createAttachment_lanzaExcepcionSiEmailNoExiste() {
        CreateAttachmentRequest request = CreateAttachmentRequest.builder()
                .emailId(emailId)
                .fileName("imagen.png")
                .fileSize(512000L)
                .mimeType("image/png")
                .storageUrl("https://ejemplo.com/imagen.png")
                .build();

        when(emailRepository.findById(emailId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.createAttachment(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(attachmentRepository, never()).save(any());
    }

    // ─── deleteAttachment ─────────────────────────────────────

    @Test
    void deleteAttachment_eliminaCuandoExiste() {
        when(attachmentRepository.existsById(attachmentId)).thenReturn(true);

        attachmentService.deleteAttachment(attachmentId);

        verify(attachmentRepository).deleteById(attachmentId);
    }

    @Test
    void deleteAttachment_lanzaExcepcionCuandoNoExiste() {
        when(attachmentRepository.existsById(attachmentId)).thenReturn(false);

        assertThatThrownBy(() -> attachmentService.deleteAttachment(attachmentId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(attachmentRepository, never()).deleteById(any());
    }
}