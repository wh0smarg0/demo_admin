package org.example.admin.controllers;

import org.example.admin.dto.ADOrderCreateDTO;
import org.example.admin.dto.ADOrderTableDTO;
import org.example.admin.models.ADOrderTable;
import org.example.admin.services.ADOrderTableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order_table")
public class ADOrderTableController {

  private final ADOrderTableService service;

  public ADOrderTableController(ADOrderTableService service) {
    this.service = service;
  }

  @GetMapping
  public List<ADOrderTableDTO> getAllOrders() {
    return service.getAllOrders();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ADOrderTableDTO> getOrderById(@PathVariable Long id) {
    return service.getOrderById(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ADOrderTableDTO createOrder(@RequestBody ADOrderCreateDTO orderCreateDto) {
    System.out.println("Received orderCreateDto: " + orderCreateDto); // ← лог DTO
    ADOrderTable savedOrder = service.createOrder(orderCreateDto);
    return service.convertToDTO(savedOrder);
  }



  @PutMapping("/{id}")
  public ResponseEntity<ADOrderTableDTO> updateOrder(@PathVariable Long id, @RequestBody ADOrderTableDTO updatedOrderDto) {
    try {
      ADOrderTableDTO updatedOrder = service.updateOrder(id, updatedOrderDto);
      return ResponseEntity.ok(updatedOrder);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
    try {
      service.deleteOrder(id);
      return ResponseEntity.ok().build();
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
