package com.main;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.main.services.VendingPointService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VendingPointServiceTest extends SpringBootApplicationTest {
    @Autowired
    private VendingPointService vendingPointService;

    @Test
    void getOrdersDoneDtoByUserId_ReturnsListWithOneOrder() throws Exception {
        setupDb();
        ResponseEntity<Object> vendingPoints = vendingPointService.getAll();
        var r = vendingPoints.getBody();
        var r1 = 1;
    }
}
