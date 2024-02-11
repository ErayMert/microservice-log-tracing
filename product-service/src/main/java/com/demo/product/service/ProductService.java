package com.demo.product.service;

import com.demo.product.entity.Product;
import com.demo.product.mapper.ProductMapper;
import com.demo.product.model.CreateProductRequest;
import com.demo.product.model.ProductDto;
import com.demo.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    public ProductDto createProduct(CreateProductRequest request) {

        Product product = productRepository.save(productMapper.createProductRequestToProduct(request));

        log.info("created product");
        return productMapper.productToProductDto(product);
    }

    public List<ProductDto> getProductsByIds(List<Long> productIds){

        log.info("get products by id list");

        return productMapper.productsToProductDtoList(productRepository.findByIdIn(productIds));
    }
}
