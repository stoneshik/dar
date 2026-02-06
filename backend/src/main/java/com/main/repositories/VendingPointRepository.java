package com.main.repositories;

import com.main.entities.vendingPoints.VendingPointWithFunctionVariant;

import java.util.List;

public interface VendingPointRepository {
    List<VendingPointWithFunctionVariant> getAll();

    List<VendingPointWithFunctionVariant> getPointsForPrint();

    List<VendingPointWithFunctionVariant> getPointsForScan();
}
