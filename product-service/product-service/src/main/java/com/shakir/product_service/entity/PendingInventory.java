package com.shakir.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pending_inventory")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PendingInventory {
    @Id
    private String id;
    private String productId;
    private long quantity;
    private String status;
}
