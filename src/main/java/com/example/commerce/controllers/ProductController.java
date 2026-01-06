package com.example.commerce.controllers;

import com.example.commerce.dtos.ProductRequestDTO;
import com.example.commerce.dtos.ProductResponseDTO;
import com.example.commerce.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @RequestParam(required = false, defaultValue = "", name = "sort") String sort
    ) {
        logger.info("GET /products - Fetching all products");
        List<ProductResponseDTO> products = productService.getAllProducts(sort);
        logger.info("GET /products - Returning {} products", products.size());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        logger.info("GET /products/{} - Fetching product by id", id);
        ProductResponseDTO product = productService.getProductById(id);
        logger.info("GET /products/{} - Product found", id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        logger.info("POST /products - Creating new product with name: {}", productRequestDTO.getName());
        ProductResponseDTO createdProduct = productService.createProduct(productRequestDTO);
        logger.info("POST /products - Product created with id: {}", createdProduct.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO productRequestDTO) {
        logger.info("PUT /products/{} - Updating product", id);
        ProductResponseDTO updatedProduct = productService.updateProduct(id, productRequestDTO);
        logger.info("PUT /products/{} - Product updated successfully", id);
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ProductResponseDTO> partialUpdateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO productRequestDTO) {
        logger.info("PATCH /products/{} - Partially updating product", id);
        ProductResponseDTO updatedProduct = productService.updateProduct(id, productRequestDTO);
        logger.info("PATCH /products/{} - Product partially updated successfully", id);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("DELETE /products/{} - Deleting product", id);
        productService.deleteProduct(id);
        logger.info("DELETE /products/{} - Product deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
