package co.jtmail.dto;

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
public class UserDTO {
    private UUID idUser;
    private String email;
    private String fullName;
    private String passwordHash;
    private String avatarUrl;
    private Boolean isActive;
    private Integer unreadCount;
    private Instant createdAt;
    private Instant updatedAt;
}
