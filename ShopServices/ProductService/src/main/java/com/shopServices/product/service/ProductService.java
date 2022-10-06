package com.shopServices.product.service;

import com.shopServices.product.dto.ProductRequest;
import com.shopServices.product.dto.ProductResponse;
import com.shopServices.product.model.Product;
import com.shopServices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    public ResponseEntity<?> createProduct(ProductRequest productRequest) {

        /* Create product with builder */
        Product productForRegistration = Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .description(productRequest.getDescription())
                .build();

        /* Save product to database */
        productRepository.saveAndFlush(productForRegistration);

        return new ResponseEntity<>("Product was registered successfully", HttpStatus.CREATED);
    }

    public List<ProductResponse> getAllProducts() {

        /* Retrieve all products from the database */
        List<Product> productList = productRepository.findAll();

        /* Map each product from the productList to a product-response entity*/
        return productList.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }
}
