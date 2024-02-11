package com.demo.order.service.consumer;

import com.demo.model.order.event.OrderEvent;
import com.demo.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "order", containerFactory = "kafkaListenerContainerFactory")
    public void consumeResetPasswordEmail(OrderEvent orderEvent) {
        log.info("Consumed Customer id {} ordered product id {}", orderEvent.getCustomerId(), orderEvent.getProductId());
        orderService.createOrder(orderEvent);
    }
}
