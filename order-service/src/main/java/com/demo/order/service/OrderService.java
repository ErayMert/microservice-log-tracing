package com.demo.order.service;


import com.demo.model.order.dto.OrderDto;
import com.demo.model.order.event.OrderEvent;
import com.demo.order.entity.Order;
import com.demo.order.mapper.OrderMapper;
import com.demo.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public void createOrder(OrderEvent orderEvent) {

        Order order = orderMapper.orderEventToOrder(orderEvent);
        orderRepository.save(order);
        log.info("Order created");
    }

    public List<OrderDto> getOrdersByCustomerId(Long customerId) {

        log.info("Get orders of customer");
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orderMapper.ordersToOrderDtoList(orders);
    }

    public Long getTotalCount() {

        log.info("Run get total count method in order service");
        return orderRepository.count();

    }
}
