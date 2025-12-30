package es.vargontoc.outbox.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String ORDER_EXCHANGE = "order_exchange";
    public static final String ORDER_QUEUE = "order_queue";
    public static final String ORDER_ROUTING_KEY = "order.created";

    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue notificationsQueue() {
        return QueueBuilder.durable(ORDER_QUEUE).build();
    }

    public Binding binding(DirectExchange ordersExchange, Queue notificationsQueue) {
        return BindingBuilder.bind(notificationsQueue).to(ordersExchange).with(ORDER_ROUTING_KEY);
    }
}
