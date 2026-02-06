package com.main.services;

import com.main.entities.account.BalanceEntity;
import com.main.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService implements AccountRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Long getAccountId(String login) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("login", login);
            return jdbcTemplate.queryForObject(
                    """
                    SELECT accounts.account_id FROM accounts
                        INNER JOIN users AS a
                        ON accounts.account_id = a.user_id
                    WHERE a.user_login = :login;""",
                    mapSqlParameterSource,
                    (rs, rowNum) -> {
                        return rs.getLong("account_id");
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public BalanceEntity getBalance(String login) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("login", login);
            return jdbcTemplate.queryForObject(
                    """
                    SELECT account_id, user_login, account_balance FROM accounts
                        INNER JOIN users AS a
                        ON accounts.account_id = a.user_id
                    WHERE a.user_login = :login;""",
                    mapSqlParameterSource,
                    (rs, rowNum) -> {
                        return new BalanceEntity(
                                rs.getLong("account_id"),
                                rs.getString("user_login"),
                                rs.getBigDecimal("account_balance")
                        );
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean updateBalance(Long accountId, BigDecimal newAccountBalance) {
        if (newAccountBalance.compareTo(new BigDecimal("0.0")) < 0) {
            return false;
        }
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("account_id", accountId);
            mapSqlParameterSource.addValue("account_balance", newAccountBalance);
            int queryResult = jdbcTemplate.update(
                    "UPDATE accounts set account_balance = :account_balance WHERE account_id = :account_id;",
                    mapSqlParameterSource
            );
            return queryResult > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
