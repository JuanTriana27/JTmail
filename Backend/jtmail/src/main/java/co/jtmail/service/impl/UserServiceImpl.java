package co.jtmail.service.impl;

import co.jtmail.dto.request.CreateUserRequest;
import co.jtmail.dto.response.UserResponse;
import co.jtmail.mapper.UserMapper;
import co.jtmail.model.User;
import co.jtmail.repository.UserRepository;
import co.jtmail.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // Listar todos los usuarios
    @Override
    public List<UserResponse> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    // Buscar por id
    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        return UserMapper.toResponse(user);
    }

    // Crear Usuario
    @Override
    public UserResponse createUser(CreateUserRequest request) {

        // Validación de negocio
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Mapear request → entity
        User user = UserMapper.toEntity(request);

        // Aquí luego irá el hash de contraseña (BCrypt)
        // user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Guardar
        user = userRepository.save(user);

        // Retornar response
        return UserMapper.toResponse(user);
    }

    // Actualizar Usuraio
    @Override
    public UserResponse updateUser(UUID id, CreateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar email si cambia
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }

        // Update controlado
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setAvatarUrl(request.getAvatarUrl());

        user = userRepository.save(user);

        return UserMapper.toResponse(user);
    }

    // Eliminar Usuaro
    @Override
    public void deleteUser(UUID id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("No existe usuario con id: " + id);
        }

        userRepository.deleteById(id);
    }
}