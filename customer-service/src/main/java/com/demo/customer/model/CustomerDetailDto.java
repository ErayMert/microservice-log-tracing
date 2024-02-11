package com.demo.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailDto {

    private Long id;
    private String name;
    private String surname;
    private Integer age;
    private List<ProductDto> products;
}
