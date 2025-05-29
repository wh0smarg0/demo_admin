package org.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.admin.dto.ADOrderCreateDTO;
import org.example.admin.dto.ADOrderTableDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Collections;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ADOrderTableFunctionalTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void fullOrderFlowTest() {
    // 1. Створення замовлення
    ADOrderCreateDTO newOrder = new ADOrderCreateDTO();
    newOrder.customerName = "John Doe";
    newOrder.email = "john@example.com";
    newOrder.phone = "111222333";
    newOrder.address = "123 Test Street";
    newOrder.deliveryType = "delivery";
    newOrder.tableNumber = null;
    newOrder.total = 100.0;
    newOrder.dishes = List.of(new ADOrderCreateDTO.DishEntry() {{ id = 1L; quantity = 2; }});

    ResponseEntity<ADOrderTableDTO> createResponse = restTemplate.postForEntity("/order_table", newOrder, ADOrderTableDTO.class);
    assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    ADOrderTableDTO createdOrder = createResponse.getBody();
    assertThat(createdOrder).isNotNull();

    // 2. Оновлення замовлення
    createdOrder.setTotal(150.0);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<ADOrderTableDTO> updateRequest = new HttpEntity<>(createdOrder, headers);

    ResponseEntity<ADOrderTableDTO> updateResponse = restTemplate.exchange(
      "/order_table/" + createdOrder.getId(),
      HttpMethod.PUT,
      updateRequest,
      ADOrderTableDTO.class);

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    ADOrderTableDTO updatedOrder = updateResponse.getBody();
    assertThat(updatedOrder).isNotNull();

    System.out.println("Updated total: " + updatedOrder.getTotal()); // Лог для перевірки

    // 3. Перевірка, що адреса оновлена
    assertThat(updatedOrder.getTotal()).isEqualTo(150.0);
  }
}

