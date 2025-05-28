package org.example.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.admin.models.ADCustomer;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ADOrderTableDTO {

  private Long id;
  private Integer tableNumber;
  private String status;
  private Double total;

  private Long customerId;         // ID клієнта
  private String customerName;
  private String customerPhone;
  private String customerEmail;
  private String customerAddress;

  private List<ADDishDTO> dishes;

  private String tableInfo;
}
