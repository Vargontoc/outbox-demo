package es.vargontoc.outbox.persistence.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import es.vargontoc.outbox.persistence.domain.OutboxEventEntity;

public interface OuboxEventJpaRepository extends JpaRepository<OutboxEventEntity, UUID> {

}
