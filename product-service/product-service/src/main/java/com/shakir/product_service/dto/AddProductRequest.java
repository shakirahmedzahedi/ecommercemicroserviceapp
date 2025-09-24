package com.shakir.product_service.dto;

import com.shakir.util_service.Category;
import com.shakir.util_service.Tags;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AddProductRequest(
        @NotBlank(message = "Title cannot be empty")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,
        @NotBlank(message = "Description cannot be empty")
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,
        @Size(max = 500, message = "Addition info cannot exceed 500 characters")
        String additionalInfo,
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String extraInfo,
        @Min(value = 1, message = "price must be at least 1")
        String price,
        @Min(value = 0, message = "discountPercentage must be at least 0")
        @Max(value = 70, message = "discountPercentage cannot exceed 70")
        String discountPercentage,
        @Pattern(regexp = "CATEGORY1|CATEGORY2|CATEGORY3|CATEGORY4", message = "Invalid category")
        String category,
        String brand,
        @Pattern(regexp = "TAG_ONE|TAG_TWO|TAG_THREE|TAG_FOUR", message = "Invalid TAG")
        String tag,
        @NotBlank(message = "Size cannot be empty")
        String size,
        @NotBlank(message = "Weight cannot be empty")
        String weight,
        @NotBlank(message = "Stock cannot be empty")
        @Size(max=5, message = "stock cannot exceed 99999")
        String stock,
        @NotBlank(message = "Thumbnail cannot be empty")
        String thumbnail,
        @NotBlank(message = "ImageList cannot be empty")
        String imagesList
) {}
