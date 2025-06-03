package org.example.admin.controllers;


import jakarta.persistence.EntityNotFoundException;
import org.example.admin.dto.IngredientDto;
import org.example.admin.dto.MenuItemUpdateDto;
import org.example.admin.models.ADCategory;
import org.example.admin.models.ADIngredient;
import org.example.admin.models.ADMenuItems;
import org.example.admin.repositories.ADCategoryRepository;
import org.example.admin.repositories.ADIngredientRepository;
import org.example.admin.repositories.ADMenuItemIngredientRepository;
import org.example.admin.repositories.ADMenuItemsRepository;
import org.example.admin.services.ADMenuItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/menu-items")
public class ADMenuItemsController {

  private final ADCategoryRepository categoryRepository;
  private final ADMenuItemsRepository menuItemsRepository;
  private final ADMenuItemIngredientRepository menuItemIngredientRepository;
  private final ADIngredientRepository ingredientRepository;
  private final ADMenuItemsService menuItemsService;

  public ADMenuItemsController(
    ADMenuItemsService menuItemsService,
    ADMenuItemsRepository menuItemsRepository,
    ADMenuItemIngredientRepository menuItemIngredientRepository,
    ADIngredientRepository ingredientRepository,
    ADCategoryRepository categoryRepository) {
    this.menuItemsService = menuItemsService;
    this.menuItemsRepository = menuItemsRepository;
    this.menuItemIngredientRepository = menuItemIngredientRepository;
    this.ingredientRepository = ingredientRepository;
    this.categoryRepository = categoryRepository;
  }

  @GetMapping
  public List<ADMenuItems> getAllMenuItems() {
    return menuItemsService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ADMenuItems> getMenuItemById(@PathVariable Long id) {
    return menuItemsService.findById(id)
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<ADMenuItems> createMenuItem(@RequestBody Map<String, Object> payload) {
    ADMenuItems item = new ADMenuItems();
    item.setName((String) payload.get("name"));
    item.setDescription((String) payload.get("description"));
    item.setImageUrl((String) payload.get("image_url"));
    item.setPrice(Double.valueOf(payload.get("price").toString()));
    item.setPreparationTime(Integer.valueOf(payload.get("preparation_time").toString()));

    if (payload.containsKey("category_id")) {
      Long categoryId = Long.valueOf(payload.get("category_id").toString());
      categoryRepository.findById(categoryId).ifPresent(category -> {
        item.setCategory(category);
        item.setCategoryName(category.getName());
      });
    }

    ADMenuItems savedItem = menuItemsService.save(item);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ADMenuItems> updateMenuItem(@PathVariable Long id, @RequestBody MenuItemUpdateDto dto) {
    Optional<ADMenuItems> updated = menuItemsService.updateMenuItem(id, dto);
    return updated.map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
    boolean deleted = menuItemsService.deleteMenuItemById(id);
    if (deleted) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  // Удаление ингредиента из всех блюд и самой базы (глобально)
  @DeleteMapping("/ingredient/{id}")
  public ResponseEntity<Void> deleteIngredientGlobally(@PathVariable Long id) {
    try {
      menuItemsService.deleteIngredientGlobally(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  // Удаление ингредиента только из конкретного блюда (удаление связи)
  @DeleteMapping("/{dishId}/ingredient/{ingredientId}")
  public ResponseEntity<Void> removeIngredientFromDish(@PathVariable Long dishId, @PathVariable Long ingredientId) {
    try {
      menuItemsService.removeIngredientFromDish(dishId, ingredientId);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{dishId}/ingredient")
  public ResponseEntity<List<Map<String, Object>>> getIngredientsByMenuItem(@PathVariable Long dishId) {
    ADMenuItems menuItem = menuItemsRepository.findById(dishId)
      .orElseThrow(() -> new EntityNotFoundException("Menu item not found"));

    List<Map<String, Object>> ingredients = menuItem.getMenuItemIngredients().stream()
      .map(link -> {
        ADIngredient ingredient = link.getIngredient();
        Map<String, Object> map = new HashMap<>();
        map.put("id", ingredient.getId());
        map.put("name", ingredient.getName());
        return map;
      })
      .collect(Collectors.toList());

    return ResponseEntity.ok(ingredients);
  }

  @PostMapping("/{id}/ingredient")
  public ResponseEntity<?> addIngredientToMenuItem(@PathVariable Long id, @RequestBody IngredientDto ingredientDto) {
    try {
      menuItemsService.addIngredientToMenuItem(id, ingredientDto.getName());
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

}

