package io.github.followsclosely.warehouse.repository;

import io.github.followsclosely.warehouse.entity.LegoElement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegoElementRepository extends JpaRepository<LegoElement, String> {
    Page<LegoElement> findByDesignLike(String design, Pageable pageable);
}

