package org.example.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ADDishDTO {
  private Long id;
  private String name;
  private Double price;
  private String description;
  private String category;
  private String imageUrl;
  private Integer quantity;
}

