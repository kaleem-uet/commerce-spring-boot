package com.example.commerce.services;

import com.example.commerce.dtos.ProductRequestDTO;
import com.example.commerce.dtos.ProductResponseDTO;
import com.example.commerce.entities.Category;
import com.example.commerce.entities.Product;
import com.example.commerce.exceptions.ResourceNotFoundException;
import com.example.commerce.mappers.ProductMapper;
import com.example.commerce.repositories.CategoryRepository;
import com.example.commerce.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, CategoryRepository categoryRepository, ImageService imageService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
    }

    public List<ProductResponseDTO> getAllProducts(String sort) {
        if (!List.of("name", "price").contains(sort)) {
            sort = "name";
        }
        logger.debug("Fetching all products from database");
        List<ProductResponseDTO> products = productRepository.findAll(Sort.by(sort))
                .stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
        logger.debug("Found {} products", products.size());
        return products;
    }

    public ProductResponseDTO getProductById(Long id) {
        logger.debug("Fetching product with id: {}", id);
        return productRepository.findById(id)
                .map(productMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        logger.debug("Creating new product with name: {}", productRequestDTO.getName());

        // Validation
        if (productRequestDTO.getName() == null || productRequestDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (productRequestDTO.getPrice() == null || productRequestDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }

        // Map DTO to entity
        Product product = productMapper.toEntity(productRequestDTO);

        // Handle category relationship
        if (productRequestDTO.getCategory() != null) {
            Category category = categoryRepository.findById(productRequestDTO.getCategory())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequestDTO.getCategory()));
            product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);
        logger.info("Product created successfully with id: {}", savedProduct.getId());
        return productMapper.toResponseDTO(savedProduct);
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        logger.debug("Updating product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Partial update - only update non-null, non-empty fields
        if (productRequestDTO.getName() != null && !productRequestDTO.getName().isEmpty()) {
            product.setName(productRequestDTO.getName());
        }
        if (productRequestDTO.getDescription() != null && !productRequestDTO.getDescription().isEmpty()) {
            product.setDescription(productRequestDTO.getDescription());
        }
        if (productRequestDTO.getPrice() != null) {
            if (productRequestDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Product price must be positive");
            }
            product.setPrice(productRequestDTO.getPrice());
        }
        if (productRequestDTO.getCategory() != null) {
            Category category = categoryRepository.findById(productRequestDTO.getCategory())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequestDTO.getCategory()));
            product.setCategory(category);
        }

        Product updatedProduct = productRepository.save(product);
        logger.info("Product updated successfully with id: {}", id);
        return productMapper.toResponseDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        logger.debug("Deleting product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productRepository.delete(product);
        logger.info("Product deleted successfully with id: {}", id);
    }

    public ProductResponseDTO uploadProductImage(Long id, MultipartFile file) {
        logger.debug("Uploading image for product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        imageService.validateImage(file);

        try {
            byte[] imageData = imageService.readImageBytes(file);
            byte[] compressedImage = imageService.compressImage(imageData);

            product.setImageName(file.getOriginalFilename());
            product.setImageType(file.getContentType());
            product.setImageData(compressedImage);

            Product updatedProduct = productRepository.save(product);
            logger.info("Image uploaded successfully for product id: {}", id);
            return productMapper.toResponseDTO(updatedProduct);
        } catch (IOException e) {
            logger.error("Failed to upload image for product id: {}", id, e);
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    public byte[] getProductImage(Long id) {
        logger.debug("Fetching image for product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (product.getImageData() == null) {
            throw new ResourceNotFoundException("Image not found for product with id: " + id);
        }

        return product.getImageData();
    }

    public String getProductImageType(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return product.getImageType() != null ? product.getImageType() : "image/jpeg";
    }

    public void deleteProductImage(Long id) {
        logger.debug("Deleting image for product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setImageName(null);
        product.setImageType(null);
        product.setImageData(null);

        productRepository.save(product);
        logger.info("Image deleted successfully for product id: {}", id);
    }
}
