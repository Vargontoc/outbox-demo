package es.vargontoc.outbox.persistence.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected OrderEntity() {
    }

    public OrderEntity(UUID id, String status, BigDecimal totalAmount, OffsetDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

}
