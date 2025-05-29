package org.example;

import org.example.admin.dto.ADOrderCreateDTO;
import org.example.admin.dto.ADOrderTableDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ADOrderTableControllerIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void testGetAllOrders() {
    ResponseEntity<ADOrderTableDTO[]> response = restTemplate.getForEntity("/order_table", ADOrderTableDTO[].class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  public void testCreateOrder() {
    ADOrderCreateDTO newOrder = new ADOrderCreateDTO();
    newOrder.setCustomerName("Test Customer");
    newOrder.setEmail("test@example.com");
    newOrder.setPhone("1234567890");
    newOrder.setAddress("Test address");
    newOrder.setDeliveryType("pickup");
    newOrder.setTableNumber(1);
    newOrder.setDishes(java.util.Collections.emptyList());
    newOrder.setTotal(0);

    ResponseEntity<ADOrderTableDTO> response = restTemplate.postForEntity("/order_table", newOrder, ADOrderTableDTO.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isNotNull();
  }
}
