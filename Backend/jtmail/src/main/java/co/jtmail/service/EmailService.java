package co.jtmail.service;

import co.jtmail.dto.request.SendEmailRequest;
import co.jtmail.dto.response.EmailResponse;
import co.jtmail.dto.response.InboxItemResponse;

import java.util.List;
import java.util.UUID;

public interface EmailService {

    //
    EmailResponse sendEmail(UUID senderId, SendEmailRequest request);

    //
    List<InboxItemResponse> getInbox(UUID userId);

    // Obtener emails enviados
    List<EmailResponse> getSentEmails(UUID userId);

    List<InboxItemResponse> getStarred(UUID userId);
    List<InboxItemResponse> getTrash(UUID userId);
    List<EmailResponse> getDrafts(UUID userId);
    EmailResponse getEmailById(UUID emailId);
    void markAsRead(UUID recipientId);
    void toggleStar(UUID recipientId);
    void moveToTrash(UUID recipientId);
    void deleteEmail(UUID emailId);
}