package org.example.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class ADOrderCreateDTO {
  public String customerName;
  public String email;
  public String phone;
  public String address;
  public String deliveryType; // "delivery", "pickup", "inplace"
  public Integer tableNumber;
  public List<DishEntry> dishes;
  public double total;

  public static class DishEntry {
    public Long id;
    public int quantity;
  }
}

