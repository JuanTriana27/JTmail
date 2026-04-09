package co.jtmail.service.impl;

import co.jtmail.dto.request.SendEmailRequest;
import co.jtmail.dto.response.EmailResponse;
import co.jtmail.dto.response.InboxItemResponse;
import co.jtmail.exception.ResourceNotFoundException;
import co.jtmail.mapper.EmailMapper;
import co.jtmail.model.*;
import co.jtmail.model.enums.EmailStatus;
import co.jtmail.model.enums.RecipientType;
import co.jtmail.repository.*;
import co.jtmail.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;
    private final EmailRecipientRepository recipientRepository;
    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;

    // Enviar email
    @Override
    public EmailResponse sendEmail(UUID senderId, SendEmailRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", senderId));

        // Si viene threadId es una respuesta, si no se crea un hilo nuevo
        MailThread thread = resolveThread(request.getThreadId());

        Email email = Email.builder()
                .thread(thread)
                .sender(sender)
                .subject(request.getSubject())
                .body(request.getBody())
                .status(EmailStatus.SENT)
                .sentAt(Instant.now())
                .build();

        emailRepository.save(email);

        // Crear registro por cada destinatario — cada uno tiene su propio estado
        attachRecipients(email, request.getTo(), RecipientType.TO);
        attachRecipients(email, request.getCc(), RecipientType.CC);
        attachRecipients(email, request.getBcc(), RecipientType.BCC);

        return EmailMapper.toResponse(email);
    }

    // listar inbox
    @Override
    @Transactional(readOnly = true)
    public List<InboxItemResponse> getInbox(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        return recipientRepository
                .findByUserAndIsTrashedFalseAndIsArchivedFalse(user)
                .stream()
                .map(EmailMapper::toInboxItem)
                .toList();
    }

    // leido
    @Override
    @Transactional(readOnly = true)
    public List<InboxItemResponse> getStarred(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        return recipientRepository.findByUserAndIsStarredTrue(user)
                .stream()
                .map(EmailMapper::toInboxItem)
                .toList();
    }

    // papelera
    @Override
    @Transactional(readOnly = true)
    public List<InboxItemResponse> getTrash(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        return recipientRepository.findByUserAndIsTrashedTrue(user)
                .stream()
                .map(EmailMapper::toInboxItem)
                .toList();
    }

    // obtener draft
    @Override
    @Transactional(readOnly = true)
    public List<EmailResponse> getDrafts(UUID userId) {
        return emailRepository
                .findBySenderIdUserAndStatus(userId, EmailStatus.DRAFT)
                .stream()
                .map(EmailMapper::toResponse)
                .toList();
    }

    // obtener email por id
    @Override
    @Transactional(readOnly = true)
    public EmailResponse getEmailById(UUID emailId) {
        Email email = emailRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email", emailId));
        return EmailMapper.toResponse(email);
    }

    // marcar leido
    @Override
    public void markAsRead(UUID recipientId) {
        EmailRecipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient", recipientId));

        recipient.setIsRead(true);
        recipient.setReadAt(Instant.now());
        recipientRepository.save(recipient);
    }

    // marcar destacado
    @Override
    public void toggleStar(UUID recipientId) {
        EmailRecipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient", recipientId));

        // Toggle — si estaba destacado lo quita, si no lo pone
        recipient.setIsStarred(!recipient.getIsStarred());
        recipientRepository.save(recipient);
    }

    // mover a papelera
    @Override
    public void moveToTrash(UUID recipientId) {
        EmailRecipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient", recipientId));

        recipient.setIsTrashed(true);
        recipientRepository.save(recipient);
    }

    // eliminar email
    @Override
    public void deleteEmail(UUID emailId) {
        if (!emailRepository.existsById(emailId)) {
            throw new ResourceNotFoundException("Email", emailId);
        }
        emailRepository.deleteById(emailId);
    }

    // emails enviados
    @Override
    @Transactional(readOnly = true)
    public List<EmailResponse> getSentEmails(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Correos donde el usuario es el remitente y ya fueron enviados
        return emailRepository.findBySenderAndStatus(user, EmailStatus.SENT)
                .stream()
                .map(EmailMapper::toResponse)
                .toList();
    }

    // ─── helpers ──────────────────────────────────────────────

    private MailThread resolveThread(UUID threadId) {
        if (threadId == null) {
            // Correo nuevo — crea su propio hilo
            return threadRepository.save(MailThread.builder().build());
        }
        return threadRepository.findById(threadId)
                .orElseThrow(() -> new ResourceNotFoundException("Thread", threadId));
    }

    private void attachRecipients(Email email, List<UUID> userIds, RecipientType type) {
        if (userIds == null || userIds.isEmpty()) return;

        List<EmailRecipient> recipients = new ArrayList<>();
        userIds.forEach(uid -> {
            User user = userRepository.findById(uid)
                    .orElseThrow(() -> new ResourceNotFoundException("User", uid));

            recipients.add(EmailRecipient.builder()
                    .email(email)
                    .user(user)
                    .type(type)
                    .build());
        });

        recipientRepository.saveAll(recipients);
    }
}