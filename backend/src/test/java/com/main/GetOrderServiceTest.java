package com.main;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import com.main.dto.OrderScanDto;
import com.main.entities.order.OrderScanWithNumberPages;
import com.main.entities.order.OrderStatus;
import com.main.entities.order.OrderType;
import com.main.entities.order.OrderWithAddress;
import com.main.services.order.CreateOrderService;
import com.main.services.order.GetOrderService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetOrderServiceTest extends SpringBootApplicationTest {
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

    @Autowired
    private GetOrderService getOrderService;

    @Test
    void getOrderPrintById_ReturnsResponseWithStatusNotFound() throws Exception {
        setupDb(postgresSqlContainer);
        final String login = "not_found";
        ResponseEntity<Object> getResponse = getOrderService.getOrderPrintById(
            login,
            0L
        );
        ResponseMessageWrapper responseEntity = (ResponseMessageWrapper) getResponse.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode()),
            () -> assertEquals("Заказ не найден", responseEntity.responseMessage())
        );
    }

    @Test
    void getOrderScanById_ReturnsResponseWithStatusOk() throws Exception {
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
        ResponseEntity<Object> getResponse = getOrderService.getOrderScanById(
            login,
            1L
        );
        OrderScanWithNumberPages orderScan = (OrderScanWithNumberPages) getResponse.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, getResponse.getStatusCode()),
            () -> assertEquals(10L, orderScan.getNumberPages()),
            () -> assertNotNull(orderScan.getOrderInfo())
        );
        OrderWithAddress orderWithAddress = orderScan.getOrderInfo();
        assertAll(
            () -> assertEquals(1L, orderWithAddress.getOrderId()),
            () -> assertEquals(1L, orderWithAddress.getAccountId()),
            () -> assertEquals("Невский проспект, 1/4", orderWithAddress.getOrderAddress()),
            () -> assertNotNull(orderWithAddress.getOrderDatetime()),
            () -> assertEquals(OrderType.SCAN, orderWithAddress.getOrderType()),
            () -> assertEquals(OrderStatus.NOT_PAID, orderWithAddress.getOrderStatus()),
            () -> assertNotNull(orderWithAddress.getOrderNum())
        );
    }

    @Test
    void getOrderScanById_ReturnsResponseWithStatusNotFound() throws Exception {
        setupDb(postgresSqlContainer);
        final String login = "not_found";
        ResponseEntity<Object> getResponse = getOrderService.getOrderScanById(
            login,
            0L
        );
        ResponseMessageWrapper responseEntity = (ResponseMessageWrapper) getResponse.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode()),
            () -> assertEquals("Заказ не найден", responseEntity.responseMessage())
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void getOrders_ReturnsResponseWithStatusOk() throws Exception {
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
        ResponseEntity<Object> getResponse = getOrderService.getOrders(
            login,
            OrderStatus.NOT_PAID
        );
        List<OrderWithAddress> notPaidOrders = (List<OrderWithAddress>) getResponse.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, getResponse.getStatusCode()),
            () -> assertEquals(1, notPaidOrders.size())
        );
        OrderWithAddress firstNotPaidOrder = notPaidOrders.get(0);
        assertAll(
            () -> assertEquals(1L, firstNotPaidOrder.getOrderId()),
            () -> assertEquals(1L, firstNotPaidOrder.getAccountId()),
            () -> assertEquals("Невский проспект, 1/4", firstNotPaidOrder.getOrderAddress()),
            () -> assertEquals(new BigDecimal("5"), firstNotPaidOrder.getOrderAmount()),
            () -> assertNotNull(firstNotPaidOrder.getOrderDatetime()),
            () -> assertEquals(OrderType.SCAN, firstNotPaidOrder.getOrderType()),
            () -> assertEquals(OrderStatus.NOT_PAID, firstNotPaidOrder.getOrderStatus()),
            () -> assertNotNull(firstNotPaidOrder.getOrderNum())
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void getOrdersEmptyList_ReturnsResponseWithStatusOk() throws Exception {
        setupDb(postgresSqlContainer);
        final String login = "admin";
        ResponseEntity<Object> getResponse = getOrderService.getOrders(
            login,
            OrderStatus.NOT_PAID
        );
        List<OrderWithAddress> notPaidOrders = (List<OrderWithAddress>) getResponse.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, getResponse.getStatusCode()),
            () -> assertEquals(0, notPaidOrders.size())
        );
    }

    @Test
    void getOrders_ReturnsResponseWithStatusBadRequest() throws Exception {
        setupDb(postgresSqlContainer);
        final String login = "not_found";
        ResponseEntity<Object> getResponse = getOrderService.getOrders(
            login,
            OrderStatus.NOT_PAID
        );
        ResponseMessageWrapper responseEntity = (ResponseMessageWrapper) getResponse.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode()),
            () -> assertEquals("Не получилось получить информацию о заказах", responseEntity.responseMessage())
        );
    }
}
