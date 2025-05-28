package org.example.admin.services;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.admin.dto.MenuItemUpdateDto;
import org.example.admin.models.ADIngredient;
import org.example.admin.models.ADMenuItemIngredient;
import org.example.admin.models.ADMenuItems;
import org.example.admin.repositories.ADCategoryRepository;
import org.example.admin.repositories.ADIngredientRepository;
import org.example.admin.repositories.ADMenuItemIngredientRepository;
import org.example.admin.repositories.ADMenuItemsRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ADMenuItemsService {

  private final ADMenuItemsRepository menuItemsRepository;
  private final ADIngredientRepository ingredientRepository;
  private final ADMenuItemIngredientRepository menuItemIngredientRepository;
  private final ADCategoryRepository categoryRepository;

  public ADMenuItemsService(ADMenuItemsRepository menuItemsRepository,
                            ADIngredientRepository ingredientRepository,
                            ADMenuItemIngredientRepository menuItemIngredientRepository, ADCategoryRepository categoryRepository) {
    this.menuItemsRepository = menuItemsRepository;
    this.ingredientRepository = ingredientRepository;
    this.menuItemIngredientRepository = menuItemIngredientRepository;
    this.categoryRepository = categoryRepository;
  }

    public Optional<ADMenuItems> getMenuItemById(Long id) {
      return menuItemsRepository.findById(id);
    }

    public List<ADMenuItems> findAll() {
        return menuItemsRepository.findAll();
    }

    public Optional<ADMenuItems> findById(Long id) {
        return menuItemsRepository.findById(id);
    }

    public ADMenuItems save(ADMenuItems menuItem) {
        return menuItemsRepository.save(menuItem);
    }

    public void deleteById(Long id) {
        menuItemsRepository.deleteById(id);
    }

  @Transactional
  public void addIngredientToMenuItem(Long menuItemId, String ingredientName) {
    ADMenuItems menuItem = menuItemsRepository.findById(menuItemId)
      .orElseThrow(() -> new RuntimeException("Страва не знайдена"));

    // Ищем ингредиент по имени или создаём новый
    ADIngredient ingredient = ingredientRepository.findByName(ingredientName)
      .orElseGet(() -> {
        ADIngredient newIngredient = new ADIngredient();
        newIngredient.setName(ingredientName);
        return ingredientRepository.save(newIngredient);
      });

    // Создаём связь между блюдом и ингредиентом
    ADMenuItemIngredient menuItemIngredient = new ADMenuItemIngredient();
    menuItemIngredient.setMenuItem(menuItem);
    menuItemIngredient.setIngredient(ingredient);

    // Сохраняем связь в репозитории, например:
    menuItemIngredientRepository.save(menuItemIngredient);

    // Можно дополнительно обновить список в menuItem, если есть двунаправленная связь
    // menuItem.getIngredients().add(menuItemIngredient);
    // menuItemsRepository.save(menuItem);
  }

  @Transactional
  public void deleteIngredientGlobally(Long ingredientId) {
    if (!ingredientRepository.existsById(ingredientId)) {
      throw new EntityNotFoundException("Ingredient not found");
    }

    List<ADMenuItemIngredient> links = menuItemIngredientRepository.findAll().stream()
      .filter(link -> link.getIngredient().getId().equals(ingredientId))
      .toList();

    menuItemIngredientRepository.deleteAll(links);
    ingredientRepository.deleteById(ingredientId);
  }

  @Transactional
  public void removeIngredientFromDish(Long dishId, Long ingredientId) {
    if (!menuItemIngredientRepository.existsByMenuItem_IdAndIngredient_Id(dishId, ingredientId)) {
      throw new EntityNotFoundException("Link not found");
    }
    menuItemIngredientRepository.deleteByMenuItem_IdAndIngredient_Id(dishId, ingredientId);
  }

  public Optional<ADMenuItems> updateMenuItem(Long id, MenuItemUpdateDto dto) {
    return menuItemsRepository.findById(id).map(menuItem -> {
      if (dto.getName() != null) menuItem.setName(dto.getName());
      if (dto.getPrice() != null) menuItem.setPrice(dto.getPrice());
      if (dto.getDescription() != null) menuItem.setDescription(dto.getDescription());
      if (dto.getImageUrl() != null) menuItem.setImageUrl(dto.getImageUrl());
      if (dto.getPreparationTime() != null) menuItem.setPreparationTime(dto.getPreparationTime());

      if (dto.getCategoryId() != null) {
        categoryRepository.findById(dto.getCategoryId()).ifPresent(category -> {
          menuItem.setCategory(category);
          menuItem.setCategoryName(category.getName());
        });
      }
      return menuItemsRepository.save(menuItem);
    });
  }
}

