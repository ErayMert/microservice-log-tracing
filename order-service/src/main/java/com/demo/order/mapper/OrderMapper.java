package com.demo.order.mapper;

import com.demo.model.order.dto.OrderDto;
import com.demo.model.order.event.OrderEvent;
import com.demo.order.entity.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    Order orderEventToOrder(OrderEvent event);

    List<OrderDto> ordersToOrderDtoList(List<Order> orders);
}