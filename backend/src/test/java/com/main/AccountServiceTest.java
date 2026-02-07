package com.main;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import com.main.dto.ReplenishDto;
import com.main.entities.account.BalanceEntity;
import com.main.entities.replenish.ReplenishEntity;
import com.main.services.AccountService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountServiceTest extends SpringBootApplicationTest {
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
    private AccountService accountService;

    @Test
    void getBalance_ReturnsResponseWithStatusOk() throws Exception {
        setupDb(postgresSqlContainer);
        final String login = "admin";
        ResponseEntity<Object> response = accountService.getBalance(login);
        BalanceEntity balanceEntity = (BalanceEntity) response.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
            () -> assertEquals(1L, balanceEntity.getAccountId()),
            () -> assertEquals("admin", balanceEntity.getUserLogin()),
            () -> assertEquals(new BigDecimal("340"), balanceEntity.getAccountBalance())
        );
    }

    @Test
    void getBalance_ReturnsResponseWithStatusBadRequest() throws Exception {
        setupDb(postgresSqlContainer);
        final String login = "incorrect_user";
        ResponseEntity<Object> response = accountService.getBalance(login);
        ResponseMessageWrapper responseEntity = (ResponseMessageWrapper) response.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Получить информацию о балансе не получилось", responseEntity.responseMessage())
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void getReplenishes_ReturnsResponseWithOk() throws Exception {
        setupDb(postgresSqlContainer);
        final String login = "admin";
        ResponseEntity<Object> response = accountService.getReplenishes(login);
        ArrayList<ReplenishEntity> replenishes = (ArrayList<ReplenishEntity>) response.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
            () -> assertNotNull(replenishes),
            () -> assertEquals(2, replenishes.size())
        );
        ReplenishEntity firstReplenish = replenishes.get(0);
        ReplenishEntity secondReplenish = replenishes.get(1);
        assertAll(
            // 1
            () -> assertEquals(1L, firstReplenish.getReplenishId()),
            () -> assertEquals(1L, firstReplenish.getAccountId()),
            () -> assertEquals(new BigDecimal("120"), firstReplenish.getReplenishAmount()),
            () -> assertNotNull(firstReplenish.getReplenishDatetime()),
            // 2
            () -> assertEquals(2L, secondReplenish.getReplenishId()),
            () -> assertEquals(1L, secondReplenish.getAccountId()),
            () -> assertEquals(new BigDecimal("220"), secondReplenish.getReplenishAmount()),
            () -> assertNotNull(secondReplenish.getReplenishDatetime())
        );
    }

    @Test
    void getReplenishes_ReturnsResponseWithStatusNotFound() throws Exception {
        setupDb(postgresSqlContainer);
        final String login = "incorrect_user";
        ResponseEntity<Object> response = accountService.getReplenishes(login);
        ResponseMessageWrapper responseEntity = (ResponseMessageWrapper) response.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
            () -> assertEquals("Счет не найден", responseEntity.responseMessage())
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void createReplenish_ReturnsResponseWithOk() throws Exception {
        setupDb(postgresSqlContainer);
        final String login = "admin";
        final ReplenishDto replenishDto = new ReplenishDto();
        replenishDto.setReplenishAmount(new BigDecimal("100.01"));
        ResponseEntity<ResponseMessageWrapper> createResponse = accountService.createReplenish(
            replenishDto,
            login
        );
        ResponseMessageWrapper createResponseMessageWrapper = createResponse.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, createResponse.getStatusCode()),
            () -> assertNotNull(createResponseMessageWrapper),
            () -> assertEquals("Счет успешно пополнен", createResponseMessageWrapper.responseMessage())
        );
        ResponseEntity<Object> getReplenishesResponse = accountService.getReplenishes(login);
        ArrayList<ReplenishEntity> replenishes = (ArrayList<ReplenishEntity>) getReplenishesResponse.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, getReplenishesResponse.getStatusCode()),
            () -> assertNotNull(replenishes),
            () -> assertEquals(3, replenishes.size())
        );
        ResponseEntity<Object> getBalanceResponse = accountService.getBalance(login);
        BalanceEntity balanceEntity = (BalanceEntity) getBalanceResponse.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, getBalanceResponse.getStatusCode()),
            () -> assertEquals(1L, balanceEntity.getAccountId()),
            () -> assertEquals("admin", balanceEntity.getUserLogin()),
            () -> assertEquals(new BigDecimal("440.01"), balanceEntity.getAccountBalance())
        );
    }

    @Test
    void createReplenish_ReturnsResponseWithStatusNotFound() throws Exception {
        setupDb(postgresSqlContainer);
        final String login = "incorrect_user";
        final ReplenishDto replenishDto = new ReplenishDto();
        replenishDto.setReplenishAmount(new BigDecimal("100.01"));
        ResponseEntity<ResponseMessageWrapper> response = accountService.createReplenish(
            replenishDto,
            login
        );
        ResponseMessageWrapper responseEntity = (ResponseMessageWrapper) response.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
            () -> assertEquals("Счет не найден", responseEntity.responseMessage())
        );
    }

    @Test
    void createReplenish_ReturnsResponseWithStatusBadRequest() throws Exception {
        setupDb(postgresSqlContainer);
        final String login = "admin";
        final ReplenishDto replenishDto = new ReplenishDto();
        replenishDto.setReplenishAmount(new BigDecimal("-100.01"));
        ResponseEntity<ResponseMessageWrapper> response = accountService.createReplenish(
            replenishDto,
            login
        );
        ResponseMessageWrapper responseEntity = (ResponseMessageWrapper) response.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
            () -> assertEquals("Значение пополнения меньше или равно нулю", responseEntity.responseMessage())
        );
    }
}
