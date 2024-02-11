package com.demo.customer.service.producer;

import com.demo.customer.config.properties.KafkaProperties;
import com.demo.model.order.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void sendCreateOrder(OrderEvent orderEvent) {
        kafkaTemplate.send(kafkaProperties.getTopic().getOrder(), orderEvent);
        log.info("send create order: {} ", orderEvent.toString());
    }
}