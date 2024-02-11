package com.demo.customer.controller;


import com.demo.customer.model.CreateCustomerRequest;
import com.demo.customer.model.CustomerDetailDto;
import com.demo.customer.model.CustomerDto;
import com.demo.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RequestMapping("/customers")
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public CustomerDto createCustomer(CreateCustomerRequest request){
        return customerService.createCustomer(request);
    }

    @GetMapping("/{id}")
    public CustomerDetailDto getOrdersById(@PathVariable Long id){
        return customerService.getOrdersById(id);
    }

    @PostMapping("order/{customerId}/{productId}")
    public String createOrder(@PathVariable("customerId") Long customerId, @PathVariable("productId") Long productId){
        customerService.createOrder(customerId, productId);

        return "Created Order";
    }

}