package org.example.admin.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

@Getter
@Setter
@Entity
@Table(name = "menu_item_ingredient")
public class ADMenuItemIngredient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "menu_items_id")
  @JsonBackReference
  private ADMenuItems menuItem;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ingredient_id")
  @JsonBackReference
  private ADIngredient ingredient;
}



