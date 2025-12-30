package es.vargontoc.notifications.events;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.vargontoc.notifications.config.RabbitConfig;
import es.vargontoc.notifications.persistence.domain.NotificationEntity;
import es.vargontoc.notifications.persistence.domain.ProcessedEventEntity;
import es.vargontoc.notifications.persistence.repositories.NotificationRepository;
import es.vargontoc.notifications.persistence.repositories.ProcessedEventRepository;

@Component
public class OrderEventListener {

    private final ObjectMapper om = new ObjectMapper();
    private final NotificationRepository notificationRepository;
    private final ProcessedEventRepository processedEventRepository;

    public OrderEventListener(NotificationRepository notificationRepository,
            ProcessedEventRepository processedEventRepository) {
        this.notificationRepository = notificationRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NOTIFICATIONS)
    @Transactional
    public void onMessage(String payload) throws Exception {
        JsonNode jsonNode = om.readTree(payload);

        UUID eventId = UUID.fromString(jsonNode.get("eventId").asText());
        UUID orderId = UUID.fromString(jsonNode.get("orderId").asText());
        String total = jsonNode.get("totalAmount").asText();

        try {
            processedEventRepository.save(new ProcessedEventEntity(eventId, OffsetDateTime.now(ZoneOffset.UTC)));
        } catch (DataIntegrityViolationException dup) {
            return;
        }

        String message = "Order created: " + orderId + " amount=" + total;
        notificationRepository
                .save(new NotificationEntity(UUID.randomUUID(), eventId, message, OffsetDateTime.now(ZoneOffset.UTC)));
    }

}
