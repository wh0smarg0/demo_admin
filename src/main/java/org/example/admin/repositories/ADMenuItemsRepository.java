package org.example.admin.repositories;


import org.example.admin.models.ADIngredient;
import org.example.admin.models.ADMenuItemIngredient;
import org.example.admin.models.ADMenuItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ADMenuItemsRepository extends JpaRepository<ADMenuItems, Long> {

}


