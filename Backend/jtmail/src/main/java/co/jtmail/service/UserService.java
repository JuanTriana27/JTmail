package co.jtmail.service;

import co.jtmail.dto.request.CreateUserRequest;
import co.jtmail.dto.request.UpdateEmailRequest;
import co.jtmail.dto.request.UpdatePasswordRequest;
import co.jtmail.dto.request.UpdateUserRequest;
import co.jtmail.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // Listar Usuarios
    List<UserResponse> getAllUsers();

    // Consular Usuario por ID
    UserResponse getUserById(UUID id);

    // Crear usuario
    UserResponse createUser(CreateUserRequest request);

    // Actualizar usuario
    UserResponse updateUser(UUID id, UpdateUserRequest request);

    // Actualizar email
    UserResponse updateEmail(UUID id, UpdateEmailRequest request);

    // Actualizar passw
    void updatePassword(UUID id, UpdatePasswordRequest request);

    // Eliminar Usuario
    void deleteUser(UUID id);
}
