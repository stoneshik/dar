package com.main.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.ResponseMessageWrapper;
import com.main.entities.vendingPoints.VendingPointWithFunctionVariant;
import com.main.repositories.impls.VendingPointRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendingPointService {
    private final VendingPointRepositoryImpl vendingPointRepository;

    @Transactional
    public ResponseEntity<Object> getAll() {
        List<VendingPointWithFunctionVariant> vendingPoints = vendingPointRepository.getAll();
        if (vendingPoints == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не удалось получить информацию о вендинговых точках"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(vendingPoints, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> getPointsForPrint() {
        List<VendingPointWithFunctionVariant> vendingPoints = vendingPointRepository.getPointsForPrint();
        if (vendingPoints == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не удалось получить информацию о вендинговых точках"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(vendingPoints, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> getPointsForScan() {
        List<VendingPointWithFunctionVariant> vendingPoints = vendingPointRepository.getPointsForScan();
        if (vendingPoints == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не удалось получить информацию о вендинговых точках"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(vendingPoints, HttpStatus.OK);
    }
}
