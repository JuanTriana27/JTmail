package co.jtmail.service.impl;

import co.jtmail.dto.request.SendEmailRequest;
import co.jtmail.dto.response.EmailResponse;
import co.jtmail.dto.response.InboxItemResponse;
import co.jtmail.exception.ResourceNotFoundException;
import co.jtmail.model.*;
import co.jtmail.model.enums.EmailStatus;
import co.jtmail.model.enums.RecipientType;
import co.jtmail.repository.*;
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
class EmailServiceImplTest {

    @Mock private EmailRepository emailRepository;
    @Mock private EmailRecipientRepository recipientRepository;
    @Mock private UserRepository userRepository;
    @Mock private ThreadRepository threadRepository;

    @InjectMocks
    private EmailServiceImpl emailService;

    private User mockSender;
    private User mockRecipient;
    private MailThread mockThread;
    private Email mockEmail;
    private EmailRecipient mockRecipientEntry;
    private UUID senderId;
    private UUID recipientId;
    private UUID threadId;
    private UUID emailId;
    private UUID recipientEntryId;

    @BeforeEach
    void setUp() {
        senderId = UUID.randomUUID();
        recipientId = UUID.randomUUID();
        threadId = UUID.randomUUID();
        emailId = UUID.randomUUID();
        recipientEntryId = UUID.randomUUID();

        mockSender = User.builder()
                .idUser(senderId)
                .email("sender@test.com")
                .fullName("Juan Torres")
                .passwordHash("$2a$10$hash")
                .isActive(true)
                .unreadCount(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        mockRecipient = User.builder()
                .idUser(recipientId)
                .email("recipient@test.com")
                .fullName("Ana Gomez")
                .passwordHash("$2a$10$hash")
                .isActive(true)
                .unreadCount(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        mockThread = MailThread.builder()
                .idThread(threadId)
                .createdAt(Instant.now())
                .lastEmailAt(Instant.now())
                .build();

        mockEmail = Email.builder()
                .idEmail(emailId)
                .thread(mockThread)
                .sender(mockSender)
                .subject("Asunto de prueba")
                .body("Cuerpo del correo")
                .status(EmailStatus.SENT)
                .sentAt(Instant.now())
                .createdAt(Instant.now())
                .build();

        mockRecipientEntry = EmailRecipient.builder()
                .idRecipient(recipientEntryId)
                .email(mockEmail)
                .user(mockRecipient)
                .type(RecipientType.TO)
                .isRead(false)
                .isStarred(false)
                .isArchived(false)
                .isTrashed(false)
                .createdAt(Instant.now())
                .build();
    }

    // ─── sendEmail ─────────────────────────────────────────────

    @Test
    void sendEmail_creaEmailConThreadNuevoCuandoNoHayThreadId() {
        SendEmailRequest request = SendEmailRequest.builder()
                .to(List.of(recipientId))
                .subject("Asunto de prueba")
                .body("Cuerpo del correo")
                .threadId(null) // sin threadId → crea hilo nuevo
                .build();

        when(userRepository.findById(senderId)).thenReturn(Optional.of(mockSender));
        when(threadRepository.save(any(MailThread.class))).thenReturn(mockThread);
        when(emailRepository.save(any(Email.class))).thenReturn(mockEmail);
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(mockRecipient));

        EmailResponse result = emailService.sendEmail(senderId, request);

        assertThat(result).isNotNull();
        assertThat(result.getSubject()).isEqualTo("Asunto de prueba");
        verify(threadRepository).save(any(MailThread.class)); // creó thread nuevo
        verify(recipientRepository).saveAll(anyList());
    }

    @Test
    void sendEmail_reutilizaThreadExistenteCuandoEsRespuesta() {
        SendEmailRequest request = SendEmailRequest.builder()
                .to(List.of(recipientId))
                .subject("Re: Asunto")
                .body("Respuesta")
                .threadId(threadId) // responde a hilo existente
                .build();

        when(userRepository.findById(senderId)).thenReturn(Optional.of(mockSender));
        when(threadRepository.findById(threadId)).thenReturn(Optional.of(mockThread));
        when(emailRepository.save(any(Email.class))).thenReturn(mockEmail);
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(mockRecipient));

        emailService.sendEmail(senderId, request);

        // No debe crear thread nuevo — reutiliza el existente
        verify(threadRepository, never()).save(any(MailThread.class));
    }

    @Test
    void sendEmail_lanzaExcepcionSiSenderNoExiste() {
        SendEmailRequest request = SendEmailRequest.builder()
                .to(List.of(recipientId))
                .subject("Asunto")
                .body("Cuerpo")
                .build();

        when(userRepository.findById(senderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailService.sendEmail(senderId, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(emailRepository, never()).save(any());
    }

    // ─── getInbox ──────────────────────────────────────────────

    @Test
    void getInbox_retornaCorreosNoTrashedNiArchived() {
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(mockRecipient));
        when(recipientRepository.findByUserAndIsTrashedFalseAndIsArchivedFalse(mockRecipient))
                .thenReturn(List.of(mockRecipientEntry));

        List<InboxItemResponse> result = emailService.getInbox(recipientId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSubject()).isEqualTo("Asunto de prueba");
        assertThat(result.get(0).getIsRead()).isFalse();
    }

    @Test
    void getInbox_retornaListaVaciaSiNoHayCorreos() {
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(mockRecipient));
        when(recipientRepository.findByUserAndIsTrashedFalseAndIsArchivedFalse(mockRecipient))
                .thenReturn(List.of());

        assertThat(emailService.getInbox(recipientId)).isEmpty();
    }

    // ─── markAsRead ────────────────────────────────────────────

    @Test
    void markAsRead_marcaComoLeido() {
        when(recipientRepository.findById(recipientEntryId))
                .thenReturn(Optional.of(mockRecipientEntry));

        emailService.markAsRead(recipientEntryId);

        assertThat(mockRecipientEntry.getIsRead()).isTrue();
        assertThat(mockRecipientEntry.getReadAt()).isNotNull();
        verify(recipientRepository).save(mockRecipientEntry);
    }

    @Test
    void markAsRead_lanzaExcepcionSiRecipientNoExiste() {
        when(recipientRepository.findById(recipientEntryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailService.markAsRead(recipientEntryId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── toggleStar ────────────────────────────────────────────

    @Test
    void toggleStar_activaStarSiNoEstabaMarcado() {
        when(recipientRepository.findById(recipientEntryId))
                .thenReturn(Optional.of(mockRecipientEntry));

        emailService.toggleStar(recipientEntryId);

        assertThat(mockRecipientEntry.getIsStarred()).isTrue();
        verify(recipientRepository).save(mockRecipientEntry);
    }

    @Test
    void toggleStar_desactivaStarSiYaEstabaMarcado() {
        mockRecipientEntry.setIsStarred(true);
        when(recipientRepository.findById(recipientEntryId))
                .thenReturn(Optional.of(mockRecipientEntry));

        emailService.toggleStar(recipientEntryId);

        assertThat(mockRecipientEntry.getIsStarred()).isFalse();
    }

    // ─── moveToTrash ───────────────────────────────────────────

    @Test
    void moveToTrash_mandaCorreoAPapelera() {
        when(recipientRepository.findById(recipientEntryId))
                .thenReturn(Optional.of(mockRecipientEntry));

        emailService.moveToTrash(recipientEntryId);

        assertThat(mockRecipientEntry.getIsTrashed()).isTrue();
        verify(recipientRepository).save(mockRecipientEntry);
    }

    // ─── deleteEmail ───────────────────────────────────────────

    @Test
    void deleteEmail_eliminaCuandoExiste() {
        when(emailRepository.existsById(emailId)).thenReturn(true);

        emailService.deleteEmail(emailId);

        verify(emailRepository).deleteById(emailId);
    }

    @Test
    void deleteEmail_lanzaExcepcionCuandoNoExiste() {
        when(emailRepository.existsById(emailId)).thenReturn(false);

        assertThatThrownBy(() -> emailService.deleteEmail(emailId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(emailRepository, never()).deleteById(any());
    }

    // ─── getEmailById ──────────────────────────────────────────

    @Test
    void getEmailById_retornaEmailCuandoExiste() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.of(mockEmail));

        EmailResponse result = emailService.getEmailById(emailId);

        assertThat(result.getIdEmail()).isEqualTo(emailId);
        assertThat(result.getSubject()).isEqualTo("Asunto de prueba");
    }

    @Test
    void getEmailById_lanzaExcepcionCuandoNoExiste() {
        when(emailRepository.findById(emailId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailService.getEmailById(emailId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}