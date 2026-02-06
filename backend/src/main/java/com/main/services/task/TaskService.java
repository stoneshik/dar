package com.main.services.task;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


@RequiredArgsConstructor
public abstract class TaskService {
    protected final NamedParameterJdbcTemplate jdbcTemplate;

    protected Long findMachineIdForTask(Long vendingPointId, String sqlString) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("vending_point_id", vendingPointId);
            return jdbcTemplate.queryForObject(
                    sqlString,
                    mapSqlParameterSource,
                    (rs, rowNum) -> {
                        return rs.getLong("machine_id");
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
