package org.example.admin.controllers;

import org.example.admin.models.ADCategory;
import org.example.admin.repositories.ADCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class ADCategoryController {

  private final ADCategoryRepository categoryRepository;

  @Autowired
  public ADCategoryController(ADCategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  // Отримати всі категорії
  @GetMapping
  public List<ADCategory> getAllCategories() {
    return categoryRepository.findAll();
  }

  // Отримати одну категорію за ID
  @GetMapping("/{id}")
  public Optional<ADCategory> getCategoryById(@PathVariable Long id) {
    return categoryRepository.findById(id);
  }

  // Створити нову категорію
  @PostMapping
  public ADCategory createCategory(@RequestBody ADCategory category) {
    return categoryRepository.save(category);
  }

  // Оновити існуючу категорію
  @PutMapping("/{id}")
  public ADCategory updateCategory(@PathVariable Long id, @RequestBody ADCategory updatedCategory) {
    return categoryRepository.findById(id)
      .map(category -> {
        category.setName(updatedCategory.getName());
        return categoryRepository.save(category);
      })
      .orElseGet(() -> {
        updatedCategory.setId(id);
        return categoryRepository.save(updatedCategory);
      });
  }

  // Видалити категорію
  @DeleteMapping("/{id}")
  public void deleteCategory(@PathVariable Long id) {
    categoryRepository.deleteById(id);
  }
}

