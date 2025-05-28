package org.example.admin.services;


import org.example.admin.dto.IngredientDto;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.admin.models.ADIngredient;
import org.example.admin.repositories.ADIngredientRepository;
import org.example.admin.repositories.ADMenuItemIngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ADIngredientService {

    private final ADIngredientRepository ingredientRepository;
    private final ADMenuItemIngredientRepository menuItemIngredientRepository; // для перевірки використання інгредієнта

    public List<ADIngredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Optional<ADIngredient> getIngredientById(Long id) {
        return ingredientRepository.findById(id);
    }

    public ADIngredient createIngredient(ADIngredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public ADIngredient updateIngredient(Long id, IngredientDto dto) {
      return ingredientRepository.findById(id).map(ingredient -> {
        ingredient.setName(dto.getName());
        return ingredientRepository.save(ingredient);
      }).orElseThrow(() -> new RuntimeException("Ingredient not found with id " + id));
    }


  @Transactional
    public void deleteIngredientFromMenuItem(Long menuItemId, Long ingredientId) {
      boolean exists = menuItemIngredientRepository.existsByMenuItem_IdAndIngredient_Id(menuItemId, ingredientId);
      System.out.println("menuItemId = " + menuItemId + ", ingredientId = " + ingredientId);

      if (!exists) {
        throw new RuntimeException("Такого інгредієнта у цій страві немає");
      }
      System.out.println("Зв’язок існує: " + exists);

      menuItemIngredientRepository.deleteByMenuItem_IdAndIngredient_Id(menuItemId, ingredientId);
    }

}

