package com.main.services.task;

import com.main.dto.OrderPrintDto;
import com.main.dto.TaskPrintDto;
import com.main.entities.task.PrintTaskColor;
import com.main.repositories.TaskPrintRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class TaskPrintService extends TaskService implements TaskPrintRepository {
    private static final Long WRONG_ID = -1L;

    public TaskPrintService(NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private Long getIdFromQueryResult(int queryResult, KeyHolder keyHolder, String idString) {
        if (queryResult <= 0) {
            return WRONG_ID;
        }
        Map<String, Object> mapResults = keyHolder.getKeys();
        if (mapResults == null || mapResults.isEmpty()) {
            return WRONG_ID;
        }
        Long id = ((Number) mapResults.get(idString)).longValue();
        if (id < 0L) {
            return WRONG_ID;
        }
        return id;
    }

    private Long createTaskPrint(Long orderId, Long machineId, String printTaskColor, Long printTaskNumberCopies) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("order_id", orderId);
            mapSqlParameterSource.addValue("machine_id", machineId);
            mapSqlParameterSource.addValue("print_task_color", printTaskColor);
            mapSqlParameterSource.addValue("print_task_number_copies", printTaskNumberCopies);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int queryResult = jdbcTemplate.update(
                    """
                    INSERT INTO print_tasks(
                        print_task_id,
                        order_id,
                        machine_id,
                        print_task_color,
                        print_task_number_copies)
                    VALUES
                        (default, :order_id, :machine_id, :print_task_color::print_task_color_enum, :print_task_number_copies);
                    """,
                    mapSqlParameterSource,
                    keyHolder
            );
            return getIdFromQueryResult(queryResult, keyHolder, "print_task_id");
        } catch (EmptyResultDataAccessException e) {
            return WRONG_ID;
        }
    }

    private Long uploadFile(Long userId, TaskPrintDto taskPrintDto) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("user_id", userId);
            mapSqlParameterSource.addValue("file_name", taskPrintDto.getName());
            mapSqlParameterSource.addValue("file", taskPrintDto.getBlob());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int queryResult = jdbcTemplate.update(
                    """
                    INSERT INTO files(
                        file_id,
                        user_id,
                        file_name,
                        file_load_datetime,
                        file_oid)
                    VALUES
                        (default,
                        :user_id,
                        :file_name,
                        default,
                        lo_from_bytea(0, :file));
                    """,
                    mapSqlParameterSource,
                    keyHolder
            );
            return getIdFromQueryResult(queryResult, keyHolder, "file_id");
        } catch (EmptyResultDataAccessException e) {
            return WRONG_ID;
        }
    }

    private boolean createConnectionsForTaskPrint(Long fileId, Long machineId, Long printTaskId) {
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("print_task_id", printTaskId);
            mapSqlParameterSource.addValue("file_id", fileId);
            int queryResult = jdbcTemplate.update(
                    """
                    INSERT INTO print_task_files(
                        print_task_file_id,
                        print_task_id,
                        file_id)
                    VALUES
                        (default, :print_task_id, :file_id);
                    """,
                    mapSqlParameterSource
            );
            if (queryResult <= 0) {
                return false;
            }
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("machine_id", machineId);
            mapSqlParameterSource.addValue("file_id", fileId);
            int queryResult = jdbcTemplate.update(
                    """
                    INSERT INTO machine_files(
                        machine_file_id,
                        machine_id,
                        file_id)
                    VALUES
                        (default, :machine_id, :file_id);
                    """,
                    mapSqlParameterSource
            );
            return queryResult > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public Long findMachineIdForTaskPrint(OrderPrintDto orderPrintDto) {
        final Long vendingPointId = orderPrintDto.getVendingPointId();
        boolean isHavingBlackWhitePrintInOrder = false;
        boolean isHavingColorPrintInOrder = false;
        for (TaskPrintDto fileDto : orderPrintDto.getFiles()) {
            if (isHavingBlackWhitePrintInOrder && isHavingColorPrintInOrder) {
                break;
            }
            if (fileDto.getTypePrint().equals(PrintTaskColor.BLACK_WHITE.getName())) {
                isHavingBlackWhitePrintInOrder = true;
            } else if (fileDto.getTypePrint().equals(PrintTaskColor.COLOR.getName())) {
                isHavingColorPrintInOrder = true;
            }
        }
        String sqlString;
        if (!isHavingBlackWhitePrintInOrder && !isHavingColorPrintInOrder) {
            return null;
        } else if (isHavingBlackWhitePrintInOrder && isHavingColorPrintInOrder) {
            sqlString = """
                SELECT machine_id FROM function_variants
                    WHERE vending_point_id = :vending_point_id
                    AND (function_variant::function_variant_enum = 'black_white_print' OR function_variant::function_variant_enum = 'color_print')
                    LIMIT 1;""";
        } else if (isHavingBlackWhitePrintInOrder) {
            sqlString = """
                SELECT machine_id FROM function_variants
                    WHERE vending_point_id = :vending_point_id
                    AND function_variant::function_variant_enum = 'black_white_print'
                    LIMIT 1;""";
        } else {
            sqlString = """
                SELECT machine_id FROM function_variants
                    WHERE vending_point_id = :vending_point_id
                    AND function_variant::function_variant_enum = 'color_print'
                    LIMIT 1;""";
        }
        return findMachineIdForTask(vendingPointId, sqlString);
    }

    @Override
    public boolean createTasksPrint(Long orderId, Long machineId, OrderPrintDto orderPrintDto, Long userId) {
        for (TaskPrintDto fileDto: orderPrintDto.getFiles()) {
            Long printTaskId = createTaskPrint(orderId, machineId, fileDto.getTypePrint(), fileDto.getNumberCopies());
            if (Objects.equals(printTaskId, WRONG_ID)) {
                return false;
            }
            Long fileId = uploadFile(userId, fileDto);
            if (Objects.equals(fileId, WRONG_ID)) {
                return false;
            }
            if (!createConnectionsForTaskPrint(fileId, machineId, printTaskId)) {
                return false;
            }
        }
        return true;
    }
}
