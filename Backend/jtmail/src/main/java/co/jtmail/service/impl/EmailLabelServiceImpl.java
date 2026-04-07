package co.jtmail.service.impl;

import co.jtmail.dto.response.EmailLabelResponse;
import co.jtmail.exception.ConflictException;
import co.jtmail.exception.ResourceNotFoundException;
import co.jtmail.mapper.EmailLabelMapper;
import co.jtmail.model.Email;
import co.jtmail.model.EmailLabel;
import co.jtmail.model.Label;
import co.jtmail.repository.EmailLabelRepository;
import co.jtmail.repository.EmailRepository;
import co.jtmail.repository.LabelRepository;
import co.jtmail.service.EmailLabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailLabelServiceImpl implements EmailLabelService {

    private final EmailLabelRepository emailLabelRepository;
    private final EmailRepository emailRepository;
    private final LabelRepository labelRepository;

    @Override
    public List<EmailLabelResponse> getLabelsByEmail(UUID emailId) {
        Email email = emailRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email", emailId));

        return emailLabelRepository.findByEmail(email)
                .stream()
                .map(EmailLabelMapper::toResponse)
                .toList();
    }

    @Override
    public EmailLabelResponse addLabelToEmail(UUID emailId, UUID labelId) {
        Email email = emailRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email", emailId));

        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label", labelId));

        if (emailLabelRepository.existsByEmailAndLabel(email, label)) {
            throw new ConflictException("El correo ya tiene ese label asignado");
        }

        EmailLabel emailLabel = EmailLabel.builder()
                .email(email)
                .label(label)
                .user(label.getUser()) // ← user viene del label
                .build();

        return EmailLabelMapper.toResponse(emailLabelRepository.save(emailLabel));
    }

    @Override
    public void removeLabelFromEmail(UUID emailId, UUID labelId) {
        Email email = emailRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email", emailId));

        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label", labelId));

        if (!emailLabelRepository.existsByEmailAndLabel(email, label)) {
            throw new ResourceNotFoundException("EmailLabel", emailId);
        }

        emailLabelRepository.deleteByEmailAndLabel(email, label);
    }
}