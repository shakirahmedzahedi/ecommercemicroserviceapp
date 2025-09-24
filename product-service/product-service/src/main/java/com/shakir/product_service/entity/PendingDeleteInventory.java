package com.shakir.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="pending_delete_inventory")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PendingDeleteInventory {
    @Id
    private String id;
    private String productId;

}
