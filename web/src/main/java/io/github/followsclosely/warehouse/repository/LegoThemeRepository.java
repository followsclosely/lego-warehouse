package io.github.followsclosely.warehouse.repository;

import io.github.followsclosely.warehouse.entity.LegoTheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LegoThemeRepository extends JpaRepository<LegoTheme, String> {
    List<LegoTheme> findByParentId(String parentId);
}
