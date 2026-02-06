package com.main.controller;

import com.main.ResponseMessageWrapper;
import com.main.entities.vendingPoints.VendingPointWithFunctionVariant;
import com.main.services.VendingPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VendingPointController {
    private final VendingPointService vendingPointService;

    @GetMapping(
            path = "/api/open/vending_point/get_all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getAll() {
        List<VendingPointWithFunctionVariant> vendingPoints = vendingPointService.getAll();
        if (vendingPoints == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Не удалось получить информацию о вендинговых точках"),
                    HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(vendingPoints, HttpStatus.OK);
    }

    @GetMapping(
            path = "/api/vending_point/get_print",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getPointsForPrint() {
        List<VendingPointWithFunctionVariant> vendingPoints = vendingPointService.getPointsForPrint();
        if (vendingPoints == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Не удалось получить информацию о вендинговых точках"),
                    HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(vendingPoints, HttpStatus.OK);
    }

    @GetMapping(
            path = "/api/vending_point/get_scan",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getPointsForScan() {
        List<VendingPointWithFunctionVariant> vendingPoints = vendingPointService.getPointsForScan();
        if (vendingPoints == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Не удалось получить информацию о вендинговых точках"),
                    HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(vendingPoints, HttpStatus.OK);
    }
}
