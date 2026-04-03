package co.jtmail.mapper;

import co.jtmail.dto.request.CreateLabelRequest;
import co.jtmail.dto.response.LabelResponse;
import co.jtmail.model.Label;
import co.jtmail.model.User;

public class LabelMapper {

    public static Label toEntity(CreateLabelRequest request, User user) {
        return Label.builder()
                .name(request.getName())
                .color(request.getColor())
                .isSystem(false) // los labels creados por usuario nunca son de sistema
                .user(user)
                .build();
    }

    public static LabelResponse toResponse(Label label) {
        return LabelResponse.builder()
                .idLabel(label.getIdLabel())
                .name(label.getName())
                .color(label.getColor())
                .isSystem(label.getIsSystem())
                .createdAt(label.getCreatedAt())
                .userId(label.getUser().getIdUser())
                .build();
    }
}