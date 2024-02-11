package com.demo.product.mapper;


import com.demo.product.entity.Product;
import com.demo.product.model.CreateProductRequest;
import com.demo.product.model.ProductDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<ProductDto> productsToProductDtoList(List<Product> products);
    Product createProductRequestToProduct(CreateProductRequest request);

    ProductDto productToProductDto(Product product);
}