package org.example.admin.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_table")
public class ADOrderTable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer tableNumber;

  @Column(columnDefinition = "TEXT")
  private String dishesJson; // JSON рядок з масивом ID страв (залишити, якщо хочеш зберігати так)

  private Double total;

  private String status;

  @Column(name = "customer_name")  // залишаємо назву колонки такою, як є в таблиці
  private Long customerId;

}
