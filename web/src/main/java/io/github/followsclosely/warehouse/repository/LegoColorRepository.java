package io.github.followsclosely.warehouse.repository;

import io.github.followsclosely.warehouse.entity.LegoColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LegoColorRepository extends JpaRepository<LegoColor, String> {
    @Query(nativeQuery = true, value = "SELECT * FROM lego_color WHERE id IN (SELECT color_id FROM LEGO_COLOR_PROVIDER WHERE provider = :provider and provider_id = :providerId)")
    LegoColor findByProvider(@Param("provider") String provider, @Param("providerId") String providerId);
}
