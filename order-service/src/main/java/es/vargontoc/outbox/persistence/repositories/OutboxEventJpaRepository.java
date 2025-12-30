package es.vargontoc.outbox.persistence.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.vargontoc.outbox.persistence.domain.OutboxEventEntity;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, UUID> {

    Page<OutboxEventEntity> findByPublishedAtIsNullOrderByOccurredAtAsc(Pageable pageeable);
}
