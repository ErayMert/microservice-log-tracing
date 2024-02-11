package com.demo.customer.mapper;


import com.demo.customer.entity.Customer;
import com.demo.customer.model.CreateCustomerRequest;
import com.demo.customer.model.CustomerDto;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
    Customer createCustomerRequestToCustomer(CreateCustomerRequest request);

    CustomerDto customerToCustomerDto(Customer customer);
}