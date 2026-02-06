package com.main.services;

import com.main.entities.order.OrderEntity;
import com.main.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private Long randomNumOrder() {
        Random rn = new Random();
        final long minimum = 100000L;
        final long maximum = 999999L;
        return rn.nextLong(maximum - minimum + 1) + minimum;
    }

    private Long createNewOrder(Long accountId, Long vendingPointId, BigDecimal orderAmount, String orderType) {
        final Long WRONG_ID = -1L;
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("account_id", accountId);
            mapSqlParameterSource.addValue("vending_point_id", vendingPointId);
            mapSqlParameterSource.addValue("order_amount", orderAmount);
            mapSqlParameterSource.addValue("order_type", orderType);
            mapSqlParameterSource.addValue("order_num", randomNumOrder());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int queryResult = jdbcTemplate.update(
                    """
                    INSERT INTO orders(
                        order_id,
                        account_id,
                        vending_point_id,
                        order_amount,
                        order_datetime,
                        order_type,
                        order_status,
                        order_num)
                    VALUES
                        (default, :account_id, :vending_point_id, :order_amount, default, :order_type::order_type_enum, 'not_paid', :order_num);
                    """,
                    mapSqlParameterSource,
                    keyHolder
            );
            if (queryResult <= 0) {
                return WRONG_ID;
            }
            Map<String, Object> mapResults = keyHolder.getKeys();
            if (mapResults == null || mapResults.isEmpty()) {
                return WRONG_ID;
            }
            Long id = ((Number) mapResults.get("order_id")).longValue();
            if (id < 0L) {
                return WRONG_ID;
            }
            return id;
        } catch (EmptyResultDataAccessException e) {
            return WRONG_ID;
        }
    }

    @Override
    public OrderEntity getById(Long accountId, Long orderId) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("account_id", accountId);
            mapSqlParameterSource.addValue("order_id", orderId);
            return jdbcTemplate.queryForObject(
                    """
                    SELECT
                        order_id,
                        account_id,
                        vending_point_id,
                        order_amount,
                        order_datetime,
                        order_type,
                        order_status,
                        order_num FROM orders WHERE order_id = :order_id AND account_id = :account_id;""",
                    mapSqlParameterSource,
                    (rs, rowNum) -> {
                        return new OrderEntity(
                                rs.getLong("order_id"),
                                rs.getLong("account_id"),
                                rs.getLong("vending_point_id"),
                                rs.getBigDecimal("order_amount"),
                                rs.getDate("order_datetime"),
                                rs.getString("order_type"),
                                rs.getString("order_status"),
                                rs.getLong("order_num")
                        );
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean removeById(Long orderId) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("order_id", orderId);
            int queryResult = jdbcTemplate.update(
                    "DELETE FROM orders WHERE order_id = :order_id;",
                    mapSqlParameterSource
            );
            return queryResult > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public Long createNewScanOrder(Long accountId, Long vendingPointId, BigDecimal orderAmount) {
        return createNewOrder(accountId, vendingPointId, orderAmount, "scan");
    }

    @Override
    public Long createNewPrintOrder(Long accountId, Long vendingPointId, BigDecimal orderAmount) {
        return createNewOrder(accountId, vendingPointId, orderAmount, "print");
    }
}
