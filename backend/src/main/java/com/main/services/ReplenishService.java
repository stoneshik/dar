package com.main.services;

import com.main.entities.replenish.ReplenishEntity;
import com.main.repositories.ReplenishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplenishService implements ReplenishRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<ReplenishEntity> getAllReplenishesByAccountId(Long accountId) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("account_id", accountId);
            return jdbcTemplate.query(
                    """
                    SELECT replenish_id,
                           account_id,
                           replenish_amount,
                           replenish_datetime FROM replenishes
                    WHERE account_id = :account_id;""",
                    mapSqlParameterSource,
                    (rs, rowNum) -> {
                        return new ReplenishEntity(
                                rs.getLong("replenish_id"),
                                rs.getLong("account_id"),
                                rs.getBigDecimal("replenish_amount"),
                                rs.getDate("replenish_datetime")
                        );
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean createNewReplenish(Long accountId, BigDecimal replenishAmount) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("account_id", accountId);
            mapSqlParameterSource.addValue("replenish_amount", replenishAmount);
            int queryResult = jdbcTemplate.update(
                    """
                    INSERT INTO replenishes(replenish_id, account_id, replenish_amount, replenish_datetime)
                    VALUES (default, :account_id, :replenish_amount, default);""",
                    mapSqlParameterSource
            );
            return queryResult > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
