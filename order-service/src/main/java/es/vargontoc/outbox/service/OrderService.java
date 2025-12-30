package es.vargontoc.outbox.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.vargontoc.outbox.api.dto.CreateOrderRequest;
import es.vargontoc.outbox.persistence.domain.OrderEntity;
import es.vargontoc.outbox.persistence.domain.OutboxEventEntity;
import es.vargontoc.outbox.persistence.repositories.OrderJpaRepository;
import es.vargontoc.outbox.persistence.repositories.OutboxEventJpaRepository;

@Service
public class OrderService {
    private final OrderJpaRepository orderJpaRepository;
    private final OutboxEventJpaRepository ouboxEventJpaRepository;
    private final ObjectMapper objectMapper;

    public OrderService(OrderJpaRepository orderJpaRepository, OutboxEventJpaRepository ouboxEventJpaRepository,
            ObjectMapper objectMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.ouboxEventJpaRepository = ouboxEventJpaRepository;
        this.objectMapper = objectMapper;
    }

    public OrderEntity getOrder(UUID id) throws Exception {
        if (id == null) {
            throw new IllegalArgumentException("Order id cannot be null");
        }
        return orderJpaRepository.findById(id).orElseThrow(() -> new Exception("Order not found"));
    }

    @Transactional
    public OrderEntity createOrder(CreateOrderRequest request) {

        var now = OffsetDateTime.now(ZoneOffset.UTC);

        UUID orderId = UUID.randomUUID();
        var order = new OrderEntity(orderId, "CREATED", request.totalAmount(), now);
        orderJpaRepository.save(order);

        UUID eventId = UUID.randomUUID();

        // Crear objeto para el payload
        var eventData = new OrderCreatedEvent(orderId, request.totalAmount(), "CREATED", now);

        String payload;
        try {
            payload = objectMapper.writeValueAsString(eventData);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing outbox payload", e);
        }

        var outbox = new OutboxEventEntity(eventId, "Order", orderId, "OrderCreated", payload, now, null);
        ouboxEventJpaRepository.save(outbox);

        return order;
    }

    // DTO interno para el evento
    private record OrderCreatedEvent(UUID orderId, java.math.BigDecimal totalAmount, String status,
            OffsetDateTime occurredAt) {
    }
}
