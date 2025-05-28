package org.example.admin.repositories;

import org.example.admin.models.ADCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ADCategoryRepository extends JpaRepository<ADCategory, Long> {
}

