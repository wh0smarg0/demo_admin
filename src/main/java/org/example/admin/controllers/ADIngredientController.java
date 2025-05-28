package org.example.admin.controllers;


import lombok.RequiredArgsConstructor;
import org.example.admin.models.ADIngredient;
import org.example.admin.models.ADMenuItemIngredient;
import org.example.admin.repositories.ADIngredientRepository;
import org.example.admin.services.ADIngredientService;
import org.example.admin.services.ADMenuItemsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ingredient")
@RequiredArgsConstructor
public class ADIngredientController {

    private final ADIngredientService ingredientService;
    private final ADMenuItemsService menuItemsService;
    private final ADIngredientRepository ingredientRepository;

    @GetMapping("/ingredients")
    public List<Map<String, Object>> getAllIngredients() {
      return ingredientRepository.findAll().stream()
        .map(ingredient -> {
          Map<String, Object> map = new HashMap<>();
          map.put("id", ingredient.getId());
          map.put("name", ingredient.getName());
          return map;
        })
        .collect(Collectors.toList());
    }

  @GetMapping("/{id}/ingredient")
  public ResponseEntity<List<ADIngredient>> getIngredientsByMenuItemId(@PathVariable Long id) {
    return menuItemsService.getMenuItemById(id)
      .map(menuItem -> {
        List<ADIngredient> ingredients = menuItem.getMenuItemIngredients()
          .stream()
          .map(ADMenuItemIngredient::getIngredient)
          .toList();
        return ResponseEntity.ok(ingredients);
      })
      .orElse(ResponseEntity.notFound().build());
  }


  @PostMapping
    public ADIngredient createIngredient(@RequestBody ADIngredient ingredient) {
        return ingredientService.createIngredient(ingredient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ADIngredient> updateIngredient(@PathVariable Long id, @RequestBody ADIngredient updatedIngredient) {
        try {
            ADIngredient updated = ingredientService.updateIngredient(id, updatedIngredient);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

  @DeleteMapping("/menu-item/{menuItemId}/ingredient/{ingredientId}")
  public ResponseEntity<?> deleteIngredientFromMenuItem(@PathVariable Long menuItemId,
                                                        @PathVariable Long ingredientId) {
    try {
      ingredientService.deleteIngredientFromMenuItem(menuItemId, ingredientId);
      return ResponseEntity.ok().build();
    } catch (RuntimeException e) {
      // логируем ошибку, например logger.error("Ошибка удаления ингредиента из блюда", e);
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

}

