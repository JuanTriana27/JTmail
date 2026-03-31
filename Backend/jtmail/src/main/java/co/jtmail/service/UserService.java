package co.jtmail.service;

import co.jtmail.dto.request.CreateUserRequest;
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
    UserResponse updateUser(UUID id, CreateUserRequest request);

    // Eliminar Usuario
    void deleteUser(UUID id);
}
