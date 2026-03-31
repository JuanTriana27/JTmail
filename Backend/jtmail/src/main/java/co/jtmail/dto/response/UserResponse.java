package co.jtmail.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID idUser;
    private String email;
    private String fullName;
    private String avatarUrl;
    private Boolean isActive;
    private Integer unreadCount;
    private Instant createdAt;
}
