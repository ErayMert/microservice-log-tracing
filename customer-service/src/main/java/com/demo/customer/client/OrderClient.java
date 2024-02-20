package com.demo.customer.client;

import com.demo.model.order.dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(name="order-service", url = "http://localhost:8083")
public interface OrderClient {

    @GetMapping("/orders/{customerId}")
    List<OrderDto> listOrdersByCustomerId(@PathVariable Long customerId);

    @GetMapping("/orders/total-count")
    Long getTotalOrderCount();
}