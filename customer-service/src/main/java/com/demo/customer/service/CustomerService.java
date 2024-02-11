package com.demo.customer.service;

import com.demo.customer.client.OrderClient;
import com.demo.customer.client.ProductClient;
import com.demo.customer.entity.Customer;
import com.demo.customer.exception.CustomerRuntimeException;
import com.demo.customer.mapper.CustomerMapper;
import com.demo.customer.model.*;
import com.demo.customer.repository.CustomerRepository;
import com.demo.customer.service.producer.OrderEventProducer;
import com.demo.model.order.event.OrderEvent;
import com.demo.model.order.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final ProductClient productClient;
    private final OrderClient orderClient;
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final OrderEventProducer orderEventProducer;

    public CustomerDto createCustomer(CreateCustomerRequest request) {

        Customer customer = customerRepository.save(customerMapper.createCustomerRequestToCustomer(request));
        log.info("customer created");
        return customerMapper.customerToCustomerDto(customer);
    }

    public CustomerDetailDto getOrdersById(Long id) {

        log.info("get orders from customer");

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerRuntimeException("Can not find customer"));

        List<Long> productIdList = getProductIdList(id);

        List<ProductDto> products = productClient.getProductsByIds(productIdList);
        return CustomerDetailDto.builder()
                .age(customer.getAge())
                .name(customer.getName())
                .surname(customer.getSurname())
                .products(products)
                .build();
    }

    public void createOrder(Long customerId, Long productId) {
        OrderEvent orderEvent = OrderEvent.builder()
                .customerId(customerId)
                .productId(productId)
                .build();

        orderEventProducer.sendCreateOrder(orderEvent);
    }

    private List<Long> getProductIdList(Long id) {

        log.info("Called order service for orders of customer");

        return orderClient.listOrdersByCustomerId(id).stream()
                .map(OrderDto::getProductId)
                .toList();
    }
}
