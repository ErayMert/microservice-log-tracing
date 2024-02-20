package com.demo.order.controller;

import com.demo.model.order.dto.OrderDto;
import com.demo.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{customerId}")
    public List<OrderDto> getOrdersByCustomerId(@PathVariable("customerId") Long customerId){

        return orderService.getOrdersByCustomerId(customerId);
    }

    @GetMapping("/total-count")
    public Long getTotalCount(){
        return orderService.getTotalCount();
    }

}
