package com.shakir.product_service.repository;

import com.shakir.product_service.entity.PendingInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingInventoryRepository extends JpaRepository<PendingInventory, String> {
    List<PendingInventory> findAllByStatus(String pending);
}
