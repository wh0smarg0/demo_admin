package org.example.admin;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

import java.util.List;
import java.util.Optional;

import org.example.admin.controllers.ADOrderTableController;
import org.example.admin.dto.ADOrderCreateDTO;
import org.example.admin.dto.ADOrderTableDTO;
import org.example.admin.models.ADOrderTable;
import org.example.admin.services.ADOrderTableService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

public class ADOrderTableControllerTest {

  @Nested
  @ExtendWith(MockitoExtension.class)
  class OrderTableControllerUnitTest {

    @Mock
    private ADOrderTableService service;

    @InjectMocks
    private ADOrderTableController controller;

    @Test
    void testGetAllOrders() {
      ADOrderTableDTO order1 = new ADOrderTableDTO();
      ADOrderTableDTO order2 = new ADOrderTableDTO();

      when(service.getAllOrders()).thenReturn(List.of(order1, order2));

      List<ADOrderTableDTO> result = controller.getAllOrders();

      assertEquals(2, result.size());
      verify(service, times(1)).getAllOrders();
    }

    @Test
    void testGetOrderByIdFound() {
      ADOrderTableDTO orderDto = new ADOrderTableDTO();
      when(service.getOrderById(1L)).thenReturn(Optional.of(orderDto));

      ResponseEntity<ADOrderTableDTO> response = controller.getOrderById(1L);

      assertEquals(OK, response.getStatusCode());
      assertEquals(orderDto, response.getBody());
    }

    @Test
    void testGetOrderByIdNotFound() {
      when(service.getOrderById(1L)).thenReturn(Optional.empty());

      ResponseEntity<ADOrderTableDTO> response = controller.getOrderById(1L);

      assertEquals(NOT_FOUND, response.getStatusCode());
      assertNull(response.getBody());
    }

    @Test
    void testCreateOrder() {
      ADOrderCreateDTO createDto = new ADOrderCreateDTO();
      ADOrderTable savedOrder = new ADOrderTable();
      ADOrderTableDTO returnedDto = new ADOrderTableDTO();

      when(service.createOrder(createDto)).thenReturn(savedOrder);
      when(service.convertToDTO(savedOrder)).thenReturn(returnedDto);

      ADOrderTableDTO result = controller.createOrder(createDto);

      assertNotNull(result);
      verify(service).createOrder(createDto);
      verify(service).convertToDTO(savedOrder);
    }

    @Test
    void testUpdateOrderSuccess() {
      ADOrderTableDTO updateDto = new ADOrderTableDTO();

      when(service.updateOrder(1L, updateDto)).thenReturn(updateDto);

      ResponseEntity<ADOrderTableDTO> response = controller.updateOrder(1L, updateDto);

      assertEquals(OK, response.getStatusCode());
      assertEquals(updateDto, response.getBody());
    }

    @Test
    void testUpdateOrderNotFound() {
      ADOrderTableDTO updateDto = new ADOrderTableDTO();

      when(service.updateOrder(1L, updateDto)).thenThrow(new RuntimeException());

      ResponseEntity<ADOrderTableDTO> response = controller.updateOrder(1L, updateDto);

      assertEquals(NOT_FOUND, response.getStatusCode());
      assertNull(response.getBody());
    }

    @Test
    void testDeleteOrderSuccess() {
      doNothing().when(service).deleteOrder(1L);

      ResponseEntity<?> response = controller.deleteOrder(1L);

      assertEquals(OK, response.getStatusCode());
      verify(service).deleteOrder(1L);
    }

    @Test
    void testDeleteOrderFailure() {
      doThrow(new RuntimeException("Delete failed")).when(service).deleteOrder(1L);

      ResponseEntity<?> response = controller.deleteOrder(1L);

      assertEquals(BAD_REQUEST, response.getStatusCode());
      assertEquals("Delete failed", response.getBody());
    }
  }
}

