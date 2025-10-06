package io.github.followsclosely.warehouse.repository;

import io.github.followsclosely.warehouse.entity.LegoInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegoInventoryRepository extends JpaRepository<LegoInventory, String> {
    Page<LegoInventory> findByVersionLike(String version, Pageable pageable);
}

