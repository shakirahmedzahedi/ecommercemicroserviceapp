package com.shakir.product_service.repository;

import com.shakir.product_service.entity.PendingDeleteInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingDeleteInventoryRepository extends JpaRepository<PendingDeleteInventory, String> {
}
