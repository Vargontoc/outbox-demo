package es.vargontoc.notifications.persistence.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import es.vargontoc.notifications.persistence.domain.NotificationEntity;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    boolean existsByEventId(UUID eventId);
}
