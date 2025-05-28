package org.example.admin.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.admin.dto.ADOrderCreateDTO;
import org.example.admin.dto.ADOrderTableDTO;
import org.example.admin.dto.ADDishDTO;
import org.example.admin.models.ADCustomer;
import org.example.admin.models.ADDelivery;
import org.example.admin.models.ADMenuItems;
import org.example.admin.models.ADOrderTable;
import org.example.admin.repositories.ADCustomerRepository;
import org.example.admin.repositories.ADDeliveryRepository;
import org.example.admin.repositories.ADMenuItemsRepository;
import org.example.admin.repositories.ADOrderTableRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ADOrderTableService {

  private final ADOrderTableRepository repository;
  private final ObjectMapper objectMapper;
  private final ADCustomerRepository customerRepository;
  private final ADDeliveryRepository deliveryRepository;
  private final ADMenuItemsRepository menuItemsRepository;
  private final ADOrderTableRepository orderRepository;


  public ADOrderTableService(ADOrderTableRepository repository,
                             ADCustomerRepository customerRepository,
                             ObjectMapper objectMapper,
                             ADDeliveryRepository deliveryRepository,
                             ADOrderTableRepository orderTableRepository,
                             ADMenuItemsRepository menuItemsRepository,
                             ADOrderTableRepository orderRepository) {
    this.repository = repository;
    this.customerRepository = customerRepository;
    this.objectMapper = objectMapper;
    this.deliveryRepository = deliveryRepository;
    this.orderRepository = orderTableRepository;;
    this.menuItemsRepository = menuItemsRepository;
  }

  public List<ADOrderTableDTO> getAllOrders() {
    List<ADOrderTable> orders = repository.findAll();
    return orders.stream().map(this::convertToDTO).toList();
  }

  public Optional<ADOrderTableDTO> getOrderById(Long id) {
    return repository.findById(id)
      .map(order -> {
        ADOrderTableDTO dto = convertToDTO(order);

        if (order.getTableNumber() != null) {
          dto.setTableInfo(order.getTableNumber().toString());
        } else {
          boolean deliveryExists = deliveryRepository.existsByOrderId(order.getId());
          dto.setTableInfo(deliveryExists ? "Доставка" : "Самовивіз");
        }

        return dto;
      });
  }

  public ADOrderTable createOrder(ADOrderCreateDTO dto) {

    System.out.println("Creating order with tableNumber: " + dto.getTableNumber());
    System.out.println("Customer name: " + dto.getCustomerName());
    System.out.println("Customer email: " + dto.getEmail());
    System.out.println("Customer phone: " + dto.getPhone());
    System.out.println("Customer address: " + dto.getAddress());
    System.out.println("Delivery type: " + dto.getDeliveryType());
    System.out.println("Total: " + dto.getTotal());
    System.out.println("Number of dishes: " + (dto.getDishes() != null ? dto.getDishes().size() : 0));

    // 1. Зберігаємо або знаходимо клієнта
    ADCustomer customer = customerRepository.findByEmail(dto.email)
      .orElseGet(() -> {
        ADCustomer newCustomer = new ADCustomer();
        newCustomer.setEmail(dto.email);
        newCustomer.setName(dto.customerName);
        newCustomer.setPhone(dto.phone);
        newCustomer.setAddress(dto.address);
        return customerRepository.save(newCustomer);
      });

    // 2. Отримуємо повну інформацію про страви
    List<ADMenuItems> items = menuItemsRepository.findAllById(
      dto.dishes.stream().map(d -> d.id).toList()
    );

    // 3. Формуємо JSON для збереження у БД
    List<Map<String, Object>> dishesJson = new ArrayList<>();
    for (ADOrderCreateDTO.DishEntry dish : dto.dishes) {
      ADMenuItems item = items.stream()
        .filter(m -> m.getId().equals(dish.id))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Страва не знайдена: " + dish.id));

      Map<String, Object> map = new HashMap<>();
      map.put("id", item.getId());
      map.put("name", item.getName());
      map.put("price", item.getPrice());
      map.put("description", item.getDescription());
      map.put("category", item.getCategory().getName());
      map.put("imageUrl", item.getImageUrl());
      map.put("quantity", dish.quantity);
      dishesJson.add(map);
    }


    System.out.println("Delivery type: " + dto.getDeliveryType());
    System.out.println("Table number: " + dto.getTableNumber());

    // 4. Створюємо замовлення
    ADOrderTable order = new ADOrderTable();
    order.setCustomerId(customer.getId());
    order.setTableNumber("inplace".equals(dto.getDeliveryType()) ? dto.getTableNumber() : null);

    try {
      order.setDishesJson(new ObjectMapper().writeValueAsString(dishesJson));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Помилка при серіалізації страв у JSON", e);
    }

    order.setTotal(dto.total);
    order.setStatus("Новий");

    ADOrderTable savedOrder = orderRepository.save(order);

    // 5. Якщо доставка — створюємо запис у delivery
    if ("delivery".equals(dto.deliveryType)) {
      ADDelivery delivery = new ADDelivery();
      delivery.setAddress(dto.address);
      delivery.setDeliveryStatus("Очікується");  // <-- тут поправка
      delivery.setOrderId(savedOrder.getId());
      deliveryRepository.save(delivery);
    }


    return savedOrder;
  }

  public ADOrderTableDTO updateOrder(Long id, ADOrderTableDTO updatedOrderDto) {
    ADOrderTable existingOrder = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Order not found with id " + id));

    existingOrder.setTableNumber(updatedOrderDto.getTableNumber());
    existingOrder.setStatus(updatedOrderDto.getStatus());
    existingOrder.setTotal(updatedOrderDto.getTotal());

    existingOrder.setCustomerId(updatedOrderDto.getCustomerId());

    try {
      String dishesJson = objectMapper.writeValueAsString(updatedOrderDto.getDishes());
      existingOrder.setDishesJson(dishesJson);
    } catch (Exception e) {
      existingOrder.setDishesJson("[]");
    }

    ADOrderTable savedOrder = repository.save(existingOrder);
    return convertToDTO(savedOrder);
  }

  public void deleteOrder(Long id) {
    if (!repository.existsById(id)) {
      throw new RuntimeException("Order not found with id " + id);
    }
    repository.deleteById(id);
  }

  public ADOrderTableDTO convertToDTO(ADOrderTable order) {
    System.out.println("ConvertToDTO: order id = " + order.getId());
    System.out.println("ConvertToDTO: customerId = " + order.getCustomerId());

    ADOrderTableDTO dto = new ADOrderTableDTO();
    dto.setId(order.getId());
    dto.setTableNumber(order.getTableNumber());
    dto.setStatus(order.getStatus());
    dto.setTotal(order.getTotal());
    dto.setCustomerId(order.getCustomerId());

    if (order.getCustomerId() != null) {
      Optional<ADCustomer> customerOpt = customerRepository.findById(order.getCustomerId());
      if (customerOpt.isPresent()) {
        ADCustomer customer = customerOpt.get();
        System.out.println("ConvertToDTO: Found customer: " + customer.getName());
        dto.setCustomerName(customer.getName());
        dto.setCustomerPhone(customer.getPhone());
        dto.setCustomerEmail(customer.getEmail());
        dto.setCustomerAddress(customer.getAddress());
      } else {
        System.out.println("ConvertToDTO: Customer not found with id " + order.getCustomerId());
      }
    } else {
      System.out.println("ConvertToDTO: customerId is null");
    }

    String dishesJson = order.getDishesJson();
    if (dishesJson == null || dishesJson.isEmpty()) {
      dto.setDishes(Collections.emptyList());
    } else {
      try {
        List<ADDishDTO> dishes = objectMapper.readValue(dishesJson, new TypeReference<List<ADDishDTO>>() {});
        dto.setDishes(dishes);
        System.out.println("ConvertToDTO: Parsed dishes JSON: " + dishes.size() + " items");
      } catch (Exception e) {
        System.out.println("ConvertToDTO: Failed to parse dishes JSON: " + e.getMessage());
        dto.setDishes(Collections.emptyList());
      }
    }

    if (order.getTableNumber() != null) {
      dto.setTableInfo(order.getTableNumber().toString());
    } else {
      boolean deliveryExists = deliveryRepository.existsByOrderId(order.getId());
      dto.setTableInfo(deliveryExists ? "Доставка" : "Самовивіз");
    }

    System.out.println("ConvertToDTO: Finished DTO creation for order id " + order.getId());
    return dto;
  }


  private ADOrderTable convertToEntity(ADOrderTableDTO dto) {
    ADOrderTable order = new ADOrderTable();
    order.setId(dto.getId());
    order.setTableNumber(dto.getTableNumber());
    order.setStatus(dto.getStatus());
    order.setTotal(dto.getTotal());

    order.setCustomerId(dto.getCustomerId());

    try {
      String dishesJson = objectMapper.writeValueAsString(dto.getDishes());
      order.setDishesJson(dishesJson);
    } catch (Exception e) {
      order.setDishesJson("[]");
    }

    return order;
  }

}
