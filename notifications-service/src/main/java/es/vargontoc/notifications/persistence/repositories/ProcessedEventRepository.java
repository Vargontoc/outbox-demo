package es.vargontoc.notifications.persistence.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import es.vargontoc.notifications.persistence.domain.ProcessedEventEntity;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, UUID> {

}
