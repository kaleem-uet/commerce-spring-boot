package com.example.commerce.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Image file size cannot exceed 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid image type. Allowed types: JPEG, JPG, PNG, GIF, WEBP");
        }

        logger.debug("Image validation passed: {}, size: {} bytes", file.getOriginalFilename(), file.getSize());
    }

    public byte[] compressImage(byte[] imageData) {
        // For now, return the original data
        // You can implement image compression logic here if needed
        return imageData;
    }

    public byte[] readImageBytes(MultipartFile file) throws IOException {
        return file.getBytes();
    }
}
