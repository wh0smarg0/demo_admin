package org.example.admin.repositories;

import org.example.admin.models.ADIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ADIngredientRepository extends JpaRepository<ADIngredient, Long> {
  Optional<ADIngredient> findByName(String name);
}

