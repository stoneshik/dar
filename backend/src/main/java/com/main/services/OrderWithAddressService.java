package com.main.services;

import com.main.entities.order.OrderWithAddress;
import com.main.repositories.OrderWithAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderWithAddressService implements OrderWithAddressRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final AccountService accountService;

    private List<OrderWithAddress> getOrdersByLoginAndStatus(String login, String orderStatus) {
        final Long accountId = accountService.getAccountId(login);
        if (accountId == null) {
            return null;
        }
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("account_id", accountId);
            mapSqlParameterSource.addValue("order_status", orderStatus);
            return jdbcTemplate.query(
                    """
                    SELECT order_id,
                           account_id,
                           a.vending_point_address AS order_address,
                           order_amount,
                           order_datetime,
                           order_type,
                           order_status,
                           order_num FROM orders
                        INNER JOIN vending_points AS a
                        ON a.vending_point_id = orders.vending_point_id
                    WHERE orders.account_id = :account_id AND orders.order_status = :order_status::order_status_enum;""",
                    mapSqlParameterSource,
                    (rs, rowNum) -> {
                        return new OrderWithAddress(
                                rs.getLong("order_id"),
                                rs.getLong("account_id"),
                                rs.getString("order_address"),
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

    private boolean updateOrderStatus(Long orderId, String orderStatus) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("order_id", orderId);
            mapSqlParameterSource.addValue("order_status", orderStatus);
            int queryResult = jdbcTemplate.update(
                    "UPDATE orders set order_status = :order_status::order_status_enum WHERE order_id = :order_id;",
                    mapSqlParameterSource
            );
            return queryResult > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public List<OrderWithAddress> getPaidOrders(String login) {
        return getOrdersByLoginAndStatus(login, "paid");
    }

    @Override
    public List<OrderWithAddress> getNotPaidOrders(String login) {
        return getOrdersByLoginAndStatus(login, "not_paid");
    }

    @Override
    public OrderWithAddress getOrderById(String login, Long orderId) {
        final Long accountId = accountService.getAccountId(login);
        if (accountId == null) {
            return null;
        }
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("account_id", accountId);
            mapSqlParameterSource.addValue("order_id", orderId);
            return jdbcTemplate.queryForObject(
                    """
                    SELECT order_id,
                           account_id,
                           a.vending_point_address AS order_address,
                           order_amount,
                           order_datetime,
                           order_type,
                           order_status,
                           order_num FROM orders
                        INNER JOIN vending_points AS a
                        ON a.vending_point_id = orders.vending_point_id
                    WHERE orders.account_id = :account_id AND orders.order_id = :order_id;""",
                    mapSqlParameterSource,
                    (rs, rowNum) -> {
                        return new OrderWithAddress(
                                rs.getLong("order_id"),
                                rs.getLong("account_id"),
                                rs.getString("order_address"),
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
    public boolean checkOrderInfo(OrderWithAddress unverifiedOrderWithAddress) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("order_id", unverifiedOrderWithAddress.getOrderId());
            OrderWithAddress verifiedOrderWithAddress = jdbcTemplate.queryForObject(
                    """
                    SELECT order_id,
                           account_id,
                           a.vending_point_address AS order_address,
                           order_amount,
                           order_datetime,
                           order_type,
                           order_status,
                           order_num FROM orders
                        INNER JOIN vending_points AS a
                        ON a.vending_point_id = orders.vending_point_id
                    WHERE orders.order_id = :order_id""",
                    mapSqlParameterSource,
                    (rs, rowNum) -> {
                        return new OrderWithAddress(
                                rs.getLong("order_id"),
                                rs.getLong("account_id"),
                                rs.getString("order_address"),
                                rs.getBigDecimal("order_amount"),
                                rs.getDate("order_datetime"),
                                rs.getString("order_type"),
                                rs.getString("order_status"),
                                rs.getLong("order_num")
                        );
                    }
            );
            return unverifiedOrderWithAddress.equals(verifiedOrderWithAddress);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public boolean changeStatusOrderToPaid(Long orderId) {
        return updateOrderStatus(orderId, "paid");
    }

    @Override
    public boolean changeStatusOrderToCompleted(Long orderId) {
        return updateOrderStatus(orderId, "completed");
    }
}
