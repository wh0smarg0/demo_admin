package org.example.admin.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "delivery")
public class ADDelivery {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_id")
  private Long orderId;

  private String address;

  private String deliveryStatus;

  // Якщо хочеш пов’язати delivery з ADOrderTable через об’єкт (рекомендую), то зроби так:
  /*
  @OneToOne
  @JoinColumn(name = "order_id", insertable = false, updatable = false)
  private ADOrderTable order;
  */
}


