package com.demo.product.controller;

import com.demo.product.model.CreateProductRequest;
import com.demo.product.model.ProductDto;
import com.demo.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/products")
@RequiredArgsConstructor
@RestController
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductDto createProduct(@RequestBody CreateProductRequest request){
        return productService.createProduct(request);
    }

    @GetMapping("/by-ids")
    public List<ProductDto> getProductsByIds(@RequestParam("productIds") List<Long> productIds){
        return productService.getProductsByIds(productIds);
    }
}
