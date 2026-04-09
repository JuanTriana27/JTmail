package co.jtmail.mapper;

import co.jtmail.dto.response.EmailResponse;
import co.jtmail.dto.response.InboxItemResponse;
import co.jtmail.model.Email;
import co.jtmail.model.EmailRecipient;

public class EmailMapper {

    public static EmailResponse toResponse(Email email) {
        return EmailResponse.builder()
                .idEmail(email.getIdEmail())
                .threadId(email.getThread().getIdThread())
                .senderId(email.getSender().getIdUser())
                .senderName(email.getSender().getFullName())
                .senderEmail(email.getSender().getEmail())
                .subject(email.getSubject())
                .body(email.getBody())
                .status(email.getStatus())
                .sentAt(email.getSentAt())
                .createdAt(email.getCreatedAt())
                .build();
    }

    // Vista resumida para listar en la bandeja sin cargar el body completo
    public static InboxItemResponse toInboxItem(EmailRecipient recipient) {
        Email email = recipient.getEmail();
        return InboxItemResponse.builder()
                .idRecipient(recipient.getIdRecipient())
                .emailId(email.getIdEmail())
                .subject(email.getSubject())
                .senderName(email.getSender().getFullName())
                .senderEmail(email.getSender().getEmail())
                .isRead(recipient.getIsRead())
                .isStarred(recipient.getIsStarred())
                .isArchived(recipient.getIsArchived())
                .isTrashed(recipient.getIsTrashed())
                .sentAt(email.getSentAt())
                .build();
    }
}