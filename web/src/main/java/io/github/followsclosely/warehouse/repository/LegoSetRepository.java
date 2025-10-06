package io.github.followsclosely.warehouse.repository;

import io.github.followsclosely.warehouse.entity.LegoSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegoSetRepository extends JpaRepository<LegoSet, String> {
    Page<LegoSet> findByNameLike(String name, Pageable pageable);
}