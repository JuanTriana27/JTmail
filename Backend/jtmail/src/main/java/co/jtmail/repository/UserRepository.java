package co.jtmail.repository;

import co.jtmail.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Validar si un email ya existe (registro / update)
    boolean existsByEmail(String email);

    // Buscar usuario por email (login futuro)
    Optional<User> findByEmail(String email);
}
