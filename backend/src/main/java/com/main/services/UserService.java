package com.main.services;

import com.main.entities.user.UserEntity;
import com.main.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public int create(String email, String login, String password) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("email", email);
        mapSqlParameterSource.addValue("login", login);
        mapSqlParameterSource.addValue("password", password);
        return jdbcTemplate.update(
                "insert into users values(default, :email, :login, :password, default, default);",
                mapSqlParameterSource
        );
    }

    @Override
    public UserEntity findByLogin(String login) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("login", login);
            return jdbcTemplate.queryForObject(
                    "select * from users where user_login = :login;",
                    mapSqlParameterSource,
                    (rs, rowNum) -> {
                        return new UserEntity(
                                rs.getLong("user_id"),
                                rs.getString("user_email"),
                                rs.getString("user_login"),
                                rs.getString("user_password_hash"),
                                rs.getDate("user_created_datetime"),
                                rs.getString("user_status")
                        );
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public UserEntity findByEmail(String email) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("email", email);
            return jdbcTemplate.queryForObject(
                    "select * from users where user_email = :email;",
                    mapSqlParameterSource,
                    (rs, rowNum) -> {
                        return new UserEntity(
                                rs.getLong("user_id"),
                                rs.getString("user_email"),
                                rs.getString("user_login"),
                                rs.getString("user_password_hash"),
                                rs.getDate("user_created_datetime"),
                                rs.getString("user_status")
                        );
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Long getUserIdByLogin(String login) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("login", login);
            return jdbcTemplate.queryForObject(
                    "select * from users where user_login = :login;",
                    mapSqlParameterSource,
                    (rs, rowNum) -> {
                        return rs.getLong("user_id");
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
