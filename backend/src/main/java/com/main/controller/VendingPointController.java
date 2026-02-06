package com.main.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.services.VendingPointService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class VendingPointController {
    private final VendingPointService vendingPointService;

    @GetMapping(
        path = "/api/v1/open/vending-points",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getAll() {
        return vendingPointService.getAll();
    }

    @GetMapping(
        path = "/api/v1/vending-points/print",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getPointsForPrint() {
        return vendingPointService.getPointsForPrint();
    }

    @GetMapping(
        path = "/api/v1/vending-points/scan",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getPointsForScan() {
        return vendingPointService.getPointsForScan();
    }
}
