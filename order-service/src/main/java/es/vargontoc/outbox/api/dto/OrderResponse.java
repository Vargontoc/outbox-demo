package es.vargontoc.outbox.api.dto;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.OffsetDateTime;

public record OrderResponse(UUID id, String status, BigDecimal totalAmount, OffsetDateTime createdAt) {

}
