package com.main.services.task;

import com.main.repositories.TaskScanRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskScanService extends TaskService implements TaskScanRepository {
    public TaskScanService(NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public Long findMachineIdForTaskScan(Long vendingPointId) {
        return findMachineIdForTask(
                vendingPointId,
                """
                SELECT machine_id FROM function_variants
                    WHERE vending_point_id = :vending_point_id AND function_variant::function_variant_enum = 'scan' LIMIT 1;"""
        );
    }

    @Override
    public boolean createTaskScan(Long orderId, Long machineId, Long scanTaskNumberPages) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("order_id", orderId);
            mapSqlParameterSource.addValue("machine_id", machineId);
            mapSqlParameterSource.addValue("scan_task_number_pages", scanTaskNumberPages);
            int queryResult = jdbcTemplate.update(
                    """
                    INSERT INTO scan_tasks(
                        scan_task_id,
                        order_id,
                        machine_id,
                        scan_task_number_pages)
                    VALUES
                        (default, :order_id, :machine_id, :scan_task_number_pages);
                    """,
                    mapSqlParameterSource
            );
            return queryResult > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public Long getScanTaskNumberPagesByOrderId(Long orderId) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("order_id", orderId);
            return jdbcTemplate.queryForObject(
                    """
                    SELECT scan_task_number_pages FROM scan_tasks
                        INNER JOIN orders
                        ON orders.order_id = :order_id
                    WHERE orders.order_id = scan_tasks.order_id;""",
                    mapSqlParameterSource,
                    (rs, rowNum) -> {
                        return rs.getLong("scan_task_number_pages");
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
