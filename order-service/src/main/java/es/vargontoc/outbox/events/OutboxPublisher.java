package es.vargontoc.outbox.events;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import es.vargontoc.outbox.config.RabbitConfig;
import es.vargontoc.outbox.persistence.repositories.OutboxEventJpaRepository;
import jakarta.transaction.Transactional;

@Component
public class OutboxPublisher {

    private final OutboxEventJpaRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public OutboxPublisher(OutboxEventJpaRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelayString = "${outbox.publisher.fixed-delay-ms:2000}")
    @Transactional
    public void publishPending() {
        var page = repository.findByPublishedAtIsNullOrderByOccurredAtAsc(PageRequest.of(0, 20));

        if (page.isEmpty())
            return;
        var now = OffsetDateTime.now(ZoneOffset.UTC);

        for (var evt : page.getContent()) {
            rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EXCHANGE, RabbitConfig.ORDER_ROUTING_KEY,
                    evt.getPayload());
            evt.setPublishedAt(now);

        }

    }
}
