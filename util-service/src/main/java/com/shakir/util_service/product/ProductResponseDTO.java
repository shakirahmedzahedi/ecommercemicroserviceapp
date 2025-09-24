package com.shakir.util_service.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDTO(
        String id,
        String title,
        String description,
        String additionalInfo,
        String extraInfo,
        BigDecimal price,
        double discountPercentage,
        String category,
        String brand,
        String tag,
        String size,
        long weight,
        long stock,
        String thumbnail,
        List<String> imagesList
) {
}
