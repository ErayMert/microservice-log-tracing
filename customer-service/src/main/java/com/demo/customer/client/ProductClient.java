package com.demo.customer.client;

import com.demo.customer.model.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name="product-service", url = "http://localhost:8082")
public interface ProductClient {

    @GetMapping("/products/by-ids")
    List<ProductDto> getProductsByIds(@RequestParam("productIds") List<Long> productIds);
}