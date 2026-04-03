package co.jtmail.mapper;

import co.jtmail.dto.request.CreateUserRequest;
import co.jtmail.dto.response.UserResponse;
import co.jtmail.model.User;

public class UserMapper {

    // Request → Entity
    public static User toEntity(CreateUserRequest request) {
        return User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .avatarUrl(request.getAvatarUrl())
                // el service hashea password antes de guardar
                .isActive(true)
                .unreadCount(0)
                .build();
    }

    // Entity → Response
    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .idUser(user.getIdUser())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .isActive(user.getIsActive())
                .unreadCount(user.getUnreadCount())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
