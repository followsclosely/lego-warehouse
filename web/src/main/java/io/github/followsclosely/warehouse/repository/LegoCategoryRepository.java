package io.github.followsclosely.warehouse.repository;

import io.github.followsclosely.warehouse.entity.LegoCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegoCategoryRepository extends JpaRepository<LegoCategory, String> {
    Page<LegoCategory> findByNameLike(String name, Pageable pageable);
}

