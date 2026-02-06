package com.main.services;

import com.main.entities.vendingPoints.FunctionVariantEntity;
import com.main.entities.vendingPoints.VendingPointWithFunctionVariant;
import com.main.repositories.VendingPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Array;
import java.util.List;


@Service
@RequiredArgsConstructor
public class VendingPointService implements VendingPointRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private List<FunctionVariantEntity> getFunctionVariantsByVendingPointId(Long vendingPointId) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("vending_point_id", vendingPointId);
            return jdbcTemplate.query(
                    """
                    SELECT
                        function_variant_id,
                        vending_point_id,
                        machine_id,
                        function_variant FROM function_variants WHERE vending_point_id = :vending_point_id;""",
                    mapSqlParameterSource,
                    (rs, rowNum) -> {
                        return new FunctionVariantEntity(
                                rs.getLong("function_variant_id"),
                                rs.getLong("vending_point_id"),
                                rs.getLong("machine_id"),
                                rs.getString("function_variant")
                        );
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private List<VendingPointWithFunctionVariant> getAllPointsByQueryString(String queryString) {
        try {
            return jdbcTemplate.query(
                    queryString,
                    (rs, rowNum) -> {
                        final Long vendingPointId = rs.getLong("vending_point_id");
                        List<FunctionVariantEntity> functionVariants = getFunctionVariantsByVendingPointId(vendingPointId);
                        if (functionVariants == null) {
                            return null;
                        }
                        Array rawVendingPointCords = rs.getArray("vending_point_cords");
                        BigDecimal[] vendingPointCords = (BigDecimal[]) rawVendingPointCords.getArray();
                        return new VendingPointWithFunctionVariant(
                                vendingPointId,
                                rs.getString("vending_point_address"),
                                rs.getString("vending_point_description"),
                                rs.getLong("vending_point_number_machines"),
                                vendingPointCords,
                                functionVariants
                        );
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<VendingPointWithFunctionVariant> getAll() {
        return getAllPointsByQueryString(
            """
            SELECT
                vending_point_id,
                vending_point_address,
                vending_point_description,
                vending_point_number_machines,
                vending_point_cords
                FROM vending_points;"""
        );
    }

    @Override
    public List<VendingPointWithFunctionVariant> getPointsForPrint() {
        return getAllPointsByQueryString(
                """
                SELECT
                    vending_points.vending_point_id,
                    vending_point_address,
                    vending_point_description,
                    vending_point_number_machines,
                    vending_point_cords FROM vending_points
                    INNER JOIN function_variants
                    ON function_variants.vending_point_id = vending_points.vending_point_id AND (
                        function_variant = 'black_white_print' OR function_variant = 'color_print')
                GROUP BY vending_points.vending_point_id;"""
        );
    }

    @Override
    public List<VendingPointWithFunctionVariant> getPointsForScan() {
        return getAllPointsByQueryString(
                """
                SELECT
                    vending_points.vending_point_id,
                    vending_point_address,
                    vending_point_description,
                    vending_point_number_machines,
                    vending_point_cords FROM vending_points
                    INNER JOIN function_variants
                    ON function_variants.vending_point_id = vending_points.vending_point_id AND function_variant = 'scan'
                GROUP BY vending_points.vending_point_id;"""
        );
    }
}
