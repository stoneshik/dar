package com.main;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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

import com.main.entities.vendingPoints.VendingPointWithFunctionVariant;
import com.main.services.VendingPointService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VendingPointServiceTest extends SpringBootApplicationTest {
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
    private VendingPointService vendingPointService;

    @SuppressWarnings("unchecked")
    @Test
    void getAll_ReturnsResponseWithStatusOk() throws Exception {
        setupDb(postgresSqlContainer);
        ResponseEntity<Object> rawVendingPoints = vendingPointService.getAll();
        ArrayList<VendingPointWithFunctionVariant> vendingPoints = (ArrayList<VendingPointWithFunctionVariant>) rawVendingPoints.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, rawVendingPoints.getStatusCode()),
            () -> assertNotNull(vendingPoints),
            () -> assertEquals(4, vendingPoints.size())
        );
        VendingPointWithFunctionVariant firstVendingPoint = vendingPoints.get(0);
        VendingPointWithFunctionVariant secondVendingPoint = vendingPoints.get(1);
        VendingPointWithFunctionVariant thirdVendingPoint = vendingPoints.get(2);
        VendingPointWithFunctionVariant fourthVendingPoint = vendingPoints.get(3);
        assertAll(
            // 1
            () -> assertEquals(0L, firstVendingPoint.getVendingPointId()),
            () -> assertEquals("Невский проспект, 1/4", firstVendingPoint.getVendingPointAddress()),
            () -> assertEquals("Находится рядом с банкоматом", firstVendingPoint.getVendingPointDescription()),
            () -> assertEquals(2L, firstVendingPoint.getVendingPointNumberMachines()),
            () -> assertArrayEquals(
                new BigDecimal[]{new BigDecimal("59.936846"), new BigDecimal("30.312185")},
                firstVendingPoint.getVendingPointCords()
            ),
            () -> assertNotNull(firstVendingPoint.getFunctionVariants()),
            () -> assertEquals(5, firstVendingPoint.getFunctionVariants().size()),
            // 2
            () -> assertEquals(1L, secondVendingPoint.getVendingPointId()),
            () -> assertEquals("Невский пр-кт, 2", secondVendingPoint.getVendingPointAddress()),
            () -> assertEquals("Находится рядом с банкоматом", secondVendingPoint.getVendingPointDescription()),
            () -> assertEquals(2L, secondVendingPoint.getVendingPointNumberMachines()),
            () -> assertArrayEquals(
                new BigDecimal[]{new BigDecimal("59.937594"), new BigDecimal("30.313631")},
                secondVendingPoint.getVendingPointCords()
            ),
            () -> assertNotNull(secondVendingPoint.getFunctionVariants()),
            () -> assertEquals(2, secondVendingPoint.getFunctionVariants().size()),
            // 3
            () -> assertEquals(2L, thirdVendingPoint.getVendingPointId()),
            () -> assertEquals("Кронверкский проспект, 21/2", thirdVendingPoint.getVendingPointAddress()),
            () -> assertEquals("Находится во дворе", thirdVendingPoint.getVendingPointDescription()),
            () -> assertEquals(2L, thirdVendingPoint.getVendingPointNumberMachines()),
            () -> assertArrayEquals(
                new BigDecimal[]{new BigDecimal("59.956940"), new BigDecimal("30.319282")},
                thirdVendingPoint.getVendingPointCords()
            ),
            () -> assertNotNull(thirdVendingPoint.getFunctionVariants()),
            () -> assertEquals(1, thirdVendingPoint.getFunctionVariants().size()),
            // 4
            () -> assertEquals(3L, fourthVendingPoint.getVendingPointId()),
            () -> assertEquals("Комендантcкий пр-кт, 34", fourthVendingPoint.getVendingPointAddress()),
            () -> assertEquals("Находится в подвале рядом с шаурмечной", fourthVendingPoint.getVendingPointDescription()),
            () -> assertEquals(1L, fourthVendingPoint.getVendingPointNumberMachines()),
            () -> assertArrayEquals(
                new BigDecimal[]{new BigDecimal("60.021227"), new BigDecimal("30.243383")},
                fourthVendingPoint.getVendingPointCords()
            ),
            () -> assertNotNull(fourthVendingPoint.getFunctionVariants()),
            () -> assertEquals(2, fourthVendingPoint.getFunctionVariants().size())
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAllEmptyList_ReturnsResponseWithStatusOk() throws Exception {
        setupEmptyDb(postgresSqlContainer);
        ResponseEntity<Object> rawVendingPoints = vendingPointService.getAll();
        ArrayList<VendingPointWithFunctionVariant> vendingPoints = (ArrayList<VendingPointWithFunctionVariant>) rawVendingPoints.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, rawVendingPoints.getStatusCode()),
            () -> assertNotNull(vendingPoints),
            () -> assertEquals(0, vendingPoints.size())
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void getPointsForPrint_ReturnsResponseWithStatusOk() throws Exception {
        setupDb(postgresSqlContainer);
        ResponseEntity<Object> rawVendingPoints = vendingPointService.getPointsForPrint();
        ArrayList<VendingPointWithFunctionVariant> vendingPoints = (ArrayList<VendingPointWithFunctionVariant>) rawVendingPoints.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, rawVendingPoints.getStatusCode()),
            () -> assertNotNull(vendingPoints),
            () -> assertEquals(4, vendingPoints.size())
        );
        VendingPointWithFunctionVariant firstVendingPoint = vendingPoints.get(0);
        VendingPointWithFunctionVariant secondVendingPoint = vendingPoints.get(1);
        VendingPointWithFunctionVariant thirdVendingPoint = vendingPoints.get(2);
        VendingPointWithFunctionVariant fourthVendingPoint = vendingPoints.get(3);
        assertAll(
            // 1
            () -> assertEquals(0L, firstVendingPoint.getVendingPointId()),
            () -> assertEquals("Невский проспект, 1/4", firstVendingPoint.getVendingPointAddress()),
            () -> assertEquals("Находится рядом с банкоматом", firstVendingPoint.getVendingPointDescription()),
            () -> assertEquals(2L, firstVendingPoint.getVendingPointNumberMachines()),
            () -> assertArrayEquals(
                new BigDecimal[]{new BigDecimal("59.936846"), new BigDecimal("30.312185")},
                firstVendingPoint.getVendingPointCords()
            ),
            () -> assertNotNull(firstVendingPoint.getFunctionVariants()),
            () -> assertEquals(5, firstVendingPoint.getFunctionVariants().size()),
            // 2
            () -> assertEquals(1L, secondVendingPoint.getVendingPointId()),
            () -> assertEquals("Невский пр-кт, 2", secondVendingPoint.getVendingPointAddress()),
            () -> assertEquals("Находится рядом с банкоматом", secondVendingPoint.getVendingPointDescription()),
            () -> assertEquals(2L, secondVendingPoint.getVendingPointNumberMachines()),
            () -> assertArrayEquals(
                new BigDecimal[]{new BigDecimal("59.937594"), new BigDecimal("30.313631")},
                secondVendingPoint.getVendingPointCords()
            ),
            () -> assertNotNull(secondVendingPoint.getFunctionVariants()),
            () -> assertEquals(2, secondVendingPoint.getFunctionVariants().size()),
            // 3
            () -> assertEquals(2L, thirdVendingPoint.getVendingPointId()),
            () -> assertEquals("Кронверкский проспект, 21/2", thirdVendingPoint.getVendingPointAddress()),
            () -> assertEquals("Находится во дворе", thirdVendingPoint.getVendingPointDescription()),
            () -> assertEquals(2L, thirdVendingPoint.getVendingPointNumberMachines()),
            () -> assertArrayEquals(
                new BigDecimal[]{new BigDecimal("59.956940"), new BigDecimal("30.319282")},
                thirdVendingPoint.getVendingPointCords()
            ),
            () -> assertNotNull(thirdVendingPoint.getFunctionVariants()),
            () -> assertEquals(1, thirdVendingPoint.getFunctionVariants().size()),
            // 4
            () -> assertEquals(3L, fourthVendingPoint.getVendingPointId()),
            () -> assertEquals("Комендантcкий пр-кт, 34", fourthVendingPoint.getVendingPointAddress()),
            () -> assertEquals("Находится в подвале рядом с шаурмечной", fourthVendingPoint.getVendingPointDescription()),
            () -> assertEquals(1L, fourthVendingPoint.getVendingPointNumberMachines()),
            () -> assertArrayEquals(
                new BigDecimal[]{new BigDecimal("60.021227"), new BigDecimal("30.243383")},
                fourthVendingPoint.getVendingPointCords()
            ),
            () -> assertNotNull(fourthVendingPoint.getFunctionVariants()),
            () -> assertEquals(2, fourthVendingPoint.getFunctionVariants().size())
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void getPointsForPrintEmptyList_ReturnsResponseWithStatusOk() throws Exception {
        setupEmptyDb(postgresSqlContainer);
        ResponseEntity<Object> rawVendingPoints = vendingPointService.getPointsForPrint();
        ArrayList<VendingPointWithFunctionVariant> vendingPoints = (ArrayList<VendingPointWithFunctionVariant>) rawVendingPoints.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, rawVendingPoints.getStatusCode()),
            () -> assertNotNull(vendingPoints),
            () -> assertEquals(0, vendingPoints.size())
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void getPointsForScan_ReturnsResponseWithStatusOk() throws Exception {
        setupDb(postgresSqlContainer);
        ResponseEntity<Object> rawVendingPoints = vendingPointService.getPointsForScan();
        ArrayList<VendingPointWithFunctionVariant> vendingPoints = (ArrayList<VendingPointWithFunctionVariant>) rawVendingPoints.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, rawVendingPoints.getStatusCode()),
            () -> assertNotNull(vendingPoints),
            () -> assertEquals(3, vendingPoints.size())
        );
        VendingPointWithFunctionVariant firstVendingPoint = vendingPoints.get(0);
        VendingPointWithFunctionVariant secondVendingPoint = vendingPoints.get(1);
        VendingPointWithFunctionVariant thirdVendingPoint = vendingPoints.get(2);
        assertAll(
            // 1
            () -> assertEquals(0L, firstVendingPoint.getVendingPointId()),
            () -> assertEquals("Невский проспект, 1/4", firstVendingPoint.getVendingPointAddress()),
            () -> assertEquals("Находится рядом с банкоматом", firstVendingPoint.getVendingPointDescription()),
            () -> assertEquals(2L, firstVendingPoint.getVendingPointNumberMachines()),
            () -> assertArrayEquals(
                new BigDecimal[]{new BigDecimal("59.936846"), new BigDecimal("30.312185")},
                firstVendingPoint.getVendingPointCords()
            ),
            () -> assertNotNull(firstVendingPoint.getFunctionVariants()),
            () -> assertEquals(5, firstVendingPoint.getFunctionVariants().size()),
            // 2
            () -> assertEquals(1L, secondVendingPoint.getVendingPointId()),
            () -> assertEquals("Невский пр-кт, 2", secondVendingPoint.getVendingPointAddress()),
            () -> assertEquals("Находится рядом с банкоматом", secondVendingPoint.getVendingPointDescription()),
            () -> assertEquals(2L, secondVendingPoint.getVendingPointNumberMachines()),
            () -> assertArrayEquals(
                new BigDecimal[]{new BigDecimal("59.937594"), new BigDecimal("30.313631")},
                secondVendingPoint.getVendingPointCords()
            ),
            () -> assertNotNull(secondVendingPoint.getFunctionVariants()),
            () -> assertEquals(2, secondVendingPoint.getFunctionVariants().size()),
            // 3
            () -> assertEquals(3L, thirdVendingPoint.getVendingPointId()),
            () -> assertEquals("Комендантcкий пр-кт, 34", thirdVendingPoint.getVendingPointAddress()),
            () -> assertEquals("Находится в подвале рядом с шаурмечной", thirdVendingPoint.getVendingPointDescription()),
            () -> assertEquals(1L, thirdVendingPoint.getVendingPointNumberMachines()),
            () -> assertArrayEquals(
                new BigDecimal[]{new BigDecimal("60.021227"), new BigDecimal("30.243383")},
                thirdVendingPoint.getVendingPointCords()
            ),
            () -> assertNotNull(thirdVendingPoint.getFunctionVariants()),
            () -> assertEquals(2, thirdVendingPoint.getFunctionVariants().size())
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void getPointsForScanEmptyList_ReturnsResponseWithStatusOk() throws Exception {
        setupEmptyDb(postgresSqlContainer);
        ResponseEntity<Object> rawVendingPoints = vendingPointService.getPointsForScan();
        ArrayList<VendingPointWithFunctionVariant> vendingPoints = (ArrayList<VendingPointWithFunctionVariant>) rawVendingPoints.getBody();
        assertAll(
            () -> assertEquals(HttpStatus.OK, rawVendingPoints.getStatusCode()),
            () -> assertNotNull(vendingPoints),
            () -> assertEquals(0, vendingPoints.size())
        );
    }
}
