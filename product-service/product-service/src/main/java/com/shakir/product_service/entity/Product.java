package com.shakir.product_service.entity;

import com.shakir.util_service.Category;
import com.shakir.util_service.Tags;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Product {
    @Id
    private String id;
    private String title;
    private String description;
    private String additionalInfo;
    private String extraInfo;
    private BigDecimal price;
    private double discountPercentage;
    @Enumerated(EnumType.STRING)
    private Category category;
    private String brand;
    @Enumerated(EnumType.STRING)
    private Tags tag;
    private String size;
    private long weight;
    private String thumbnail;
    private String imagesList;

}
