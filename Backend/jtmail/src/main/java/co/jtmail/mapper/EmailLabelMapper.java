package co.jtmail.mapper;

import co.jtmail.dto.response.EmailLabelResponse;
import co.jtmail.model.EmailLabel;

public class EmailLabelMapper {

    public static EmailLabelResponse toResponse(EmailLabel emailLabel) {
        return EmailLabelResponse.builder()
                .emailId(emailLabel.getEmail().getIdEmail())
                .labelId(emailLabel.getLabel().getIdLabel())
                .labelName(emailLabel.getLabel().getName())
                .labelColor(emailLabel.getLabel().getColor())
                .build();
    }
}