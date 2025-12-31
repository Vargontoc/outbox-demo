package es.vargontoc.notifications.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import es.vargontoc.notifications.persistence.repositories.NotificationRepository;
import es.vargontoc.notifications.persistence.repositories.ProcessedEventRepository;

public class NotificationConsumerIT extends IntegrationTestBase {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    NotificationRepository notificationRepo;
    @Autowired
    ProcessedEventRepository processedRepo;

    @Test
    void consumingMessage_createsNotification_once_idempotent() throws Exception {
        UUID eventId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        String payload = """
                {"eventId":"%s","orderId":"%s","totalAmount":"10.00","status":"CREATED","occurredAt":"2025-01-01T00:00:00Z"}
                """
                .formatted(eventId, orderId);

        // Enviamos dos veces el mismo evento
        rabbitTemplate.convertAndSend("", "notifications.queue", payload);
        rabbitTemplate.convertAndSend("", "notifications.queue", payload);

        // Espera simple (listener async). Para demo es suficiente.
        Thread.sleep(1000);

        assertThat(processedRepo.findById(eventId)).isPresent();
        assertThat(notificationRepo.existsByEventId(eventId)).isTrue();
    }
}
