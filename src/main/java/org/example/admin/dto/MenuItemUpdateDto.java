package org.example.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MenuItemUpdateDto {
  private String name;
  private Double price;
  private String description;
  private String imageUrl;

  @JsonProperty("preparation_time")
  private Integer preparationTime;

  private Long categoryId;

}

