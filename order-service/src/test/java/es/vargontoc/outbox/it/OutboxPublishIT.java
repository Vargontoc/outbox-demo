package es.vargontoc.outbox.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import es.vargontoc.outbox.api.dto.CreateOrderRequest;
import es.vargontoc.outbox.events.OutboxPublisher;
import es.vargontoc.outbox.persistence.repositories.OutboxEventJpaRepository;
import es.vargontoc.outbox.service.OrderService;

public class OutboxPublishIT extends IntegrationTestBase {
    @Autowired
    OrderService orderService;
    @Autowired
    OutboxEventJpaRepository outboxRepo;
    @Autowired
    OutboxPublisher publisher;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void createOrder_persistsOutbox_andPublisherSendsMessage_andMarksPublished() {
        orderService.createOrder(new CreateOrderRequest(new BigDecimal("12.34")));

        var pending = outboxRepo.findByPublishedAtIsNullOrderByOccurredAtAsc(PageRequest.of(0, 10));
        assertThat(pending.getTotalElements()).isGreaterThan(0);

        // Publica (manual, porque scheduling está deshabilitado en test)
        publisher.publishPending();

        var after = outboxRepo.findByPublishedAtIsNullOrderByOccurredAtAsc(PageRequest.of(0, 10));
        assertThat(after.getTotalElements()).isEqualTo(0);

        // Consumimos 1 mensaje de la cola para validar que salió
        Object msg = rabbitTemplate.receiveAndConvert("notifications.queue");
        assertThat(msg).isNotNull();
        assertThat(msg.toString()).contains("\"eventId\"");
        assertThat(msg.toString()).contains("\"orderId\"");
    }
}
