package io.github.followsclosely.warehouse.repository;

import io.github.followsclosely.warehouse.entity.LegoPart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegoPartRepository extends JpaRepository<LegoPart, String> {
    Page<LegoPart> findByNameLike(String name, Pageable pageable);
}

