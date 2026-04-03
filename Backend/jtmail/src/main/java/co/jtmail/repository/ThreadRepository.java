package co.jtmail.repository;

import co.jtmail.model.MailThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ThreadRepository extends JpaRepository<MailThread, UUID> {
}
