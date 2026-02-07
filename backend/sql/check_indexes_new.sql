-- Пошаговое тестирование индексов - сначала все без индексов, потом все с индексами

-- Отключаем индексы для первой части тестов
SET enable_indexscan = off;
SET enable_bitmapscan = off;

-- 1. not_index user_email_index_hash
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM users WHERE user_email = 'user_50000@example.com';

-- 2. not_index user_login_index_hash
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM users WHERE user_login = 'user_75000';

-- 3. not_index order_type_index_btree
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM orders WHERE order_type = 'print';

-- 4. not_index order_status_index_btree
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM orders WHERE order_status = 'completed';

-- 5. not_index vending_point_unusual_schedule_date_index_hash
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM vending_point_unusual_schedules WHERE vending_point_unusual_schedule_date = current_date - interval '10 days';

-- 6. not_index function_variant_index_btree
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM function_variants WHERE function_variant = 'color_print';

-- 7. not_index machine_supplies_datetime_index_btree
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM machine_supplies WHERE machine_supplies_datetime BETWEEN now() - interval '30 days' AND now();

-- 8. not_index machine_condition_datetime_index_btree
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM machine_conditions WHERE machine_condition_datetime BETWEEN now() - interval '7 days' AND now();

DISCARD PLANS;

-- Включаем индексы для второй части тестов
SET enable_indexscan = on;
SET enable_bitmapscan = on;

-- 1. index user_email_index_hash
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM users WHERE user_email = 'user_50000@example.com';

-- 2. index user_login_index_hash
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM users WHERE user_login = 'user_75000';

-- 3. index order_type_index_btree
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM orders WHERE order_type = 'print';

-- 4. index order_status_index_btree
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM orders WHERE order_status = 'completed';

-- 5. index vending_point_unusual_schedule_date_index_hash
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM vending_point_unusual_schedules WHERE vending_point_unusual_schedule_date = current_date - interval '10 days';

-- 6. index function_variant_index_btree
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM function_variants WHERE function_variant = 'color_print';

-- 7. index machine_supplies_datetime_index_btree
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM machine_supplies WHERE machine_supplies_datetime BETWEEN now() - interval '30 days' AND now();

-- 8. index machine_condition_datetime_index_btree
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM machine_conditions WHERE machine_condition_datetime BETWEEN now() - interval '7 days' AND now();
