package com.main;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import com.main.dto.OrderScanDto;
import com.main.services.order.CreateOrderService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateOrderServiceTest extends SpringBootApplicationTest {
    @Container
    static final PostgreSQLContainer<?> postgresSqlContainer;

    static {
        postgresSqlContainer = new PostgreSQLContainer<>("postgres:16.4")
            .withReuse(true)
            .withDatabaseName("is_service");
        postgresSqlContainer.start();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresSqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresSqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresSqlContainer::getPassword);
    }

    @Autowired
    private CreateOrderService createOrderService;

    @Test
    void createOrderScan_ReturnsResponseWithStatusOk() throws Exception {
        setupDb(postgresSqlContainer);
        final String login = "admin";
        final OrderScanDto orderScanDto = new OrderScanDto();
        orderScanDto.setVendingPointId(0L);
        orderScanDto.setScanTaskNumberPages(10L);
        ResponseEntity<Object> response = createOrderService.createOrderScan(
            login,
            orderScanDto
        );
        ResponseMessageWrapper responseEntity = (ResponseMessageWrapper) response.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
            () -> assertEquals("Новый заказ создан", responseEntity.responseMessage())
        );
    }
}
