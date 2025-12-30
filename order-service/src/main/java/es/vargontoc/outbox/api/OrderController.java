package es.vargontoc.outbox.api;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.vargontoc.outbox.api.dto.CreateOrderRequest;
import es.vargontoc.outbox.api.dto.OrderResponse;
import es.vargontoc.outbox.persistence.domain.OrderEntity;
import es.vargontoc.outbox.service.OrderService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderEntity oe = orderService.createOrder(request);
        var body = new OrderResponse(oe.getId(), oe.getStatus(), oe.getTotalAmount(), oe.getCreatedAt());
        return ResponseEntity.created(URI.create("/api/v1/orders/" + oe.getId())).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable("id") UUID id) throws Exception {
        OrderEntity oe = orderService.getOrder(id);
        var body = new OrderResponse(oe.getId(), oe.getStatus(), oe.getTotalAmount(), oe.getCreatedAt());
        return ResponseEntity.ok(body);
    }
}
