-- Заполнение таблиц для демонстрации эффективности индексов

-- Создаем временную таблицу для конфигурации
CREATE TEMP TABLE config (
    key varchar(100) PRIMARY KEY,
    value varchar(100)
);

-- Заполняем конфигурацию
INSERT INTO config VALUES
    ('USERS_COUNT', '700000'),
    ('ORDERS_COUNT', '5000000'),
    ('MACHINE_SUPPLIES_COUNT', '2500000'),
    ('MACHINE_CONDITIONS_COUNT', '2000000'),
    ('VENDING_POINT_SCHEDULES_COUNT', '200000'),
    ('VENDING_POINT_UNUSUAL_SCHEDULES_COUNT', '100000'),
    ('FUNCTION_VARIANTS_COUNT', '150000'),
    ('VENDING_POINTS_COUNT', '3000'),
    ('MACHINES_COUNT', '20000'),
    ('SCAN_TASKS_COUNT', '500000'),
    ('PRINT_TASKS_COUNT', '500000'),
    ('FILES_COUNT', '500000'),
    ('SCAN_TASK_FILES_COUNT', '200000'),
    ('PRINT_TASK_FILES_COUNT', '200000'),
    ('MACHINE_FILES_COUNT', '100000'),
    ('DAYS_BACK', '730'),
    ('RECENT_DAYS', '30');

-- Вспомогательная функция для получения значения конфигурации
CREATE OR REPLACE FUNCTION get_config(_key varchar) RETURNS integer AS $$
DECLARE
    result integer;
BEGIN
    SELECT value::integer INTO result FROM config WHERE key = _key;
    RETURN result;
END;
$$ LANGUAGE plpgsql;

-- 1. Заполняем таблицу ролей (небольшая таблица)
INSERT INTO roles (role_name, role_description) VALUES
    ('user', 'Обычный пользователь'),
    ('admin', 'Администратор'),
    ('operator', 'Оператор');

-- 2. Генерируем пользователей (основная таблица с индексами)
INSERT INTO users (user_email, user_login, user_password_hash, user_created_datetime, user_status)
SELECT
    'user_' || seq || '@example.com',
    'user_' || seq,
    md5(random()::text),
    now() - (random() * get_config('DAYS_BACK') * interval '1 day'),
    (array['unverified','verified','banned'])[floor(random()*3)+1]::user_status_enum
FROM generate_series(1, get_config('USERS_COUNT')) seq;

-- 3. Генерируем вендинговые точки
INSERT INTO vending_points (vending_point_address, vending_point_description, vending_point_number_machines, vending_point_cords)
SELECT
    'Address ' || seq,
    'Description for point ' || seq,
    (random() * 10)::integer + 1,
    ARRAY[random() * 180 - 90, random() * 360 - 180]::decimal[]
FROM generate_series(1, get_config('VENDING_POINTS_COUNT')) seq;

-- 4. Генерируем аппараты
INSERT INTO machines (vending_point_id)
SELECT
    (random() * (get_config('VENDING_POINTS_COUNT') - 1))::integer + 1
FROM generate_series(1, get_config('MACHINES_COUNT')) seq;

-- 5. Генерируем функции аппаратов (таблица с индексом)
INSERT INTO function_variants (vending_point_id, machine_id, function_variant)
SELECT
    (random() * (get_config('VENDING_POINTS_COUNT') - 1))::integer + 1,
    (random() * (get_config('MACHINES_COUNT') - 1))::integer + 1,
    (array['black_white_print','color_print','scan'])[floor(random()*3)+1]::function_variant_enum
FROM generate_series(1, get_config('FUNCTION_VARIANTS_COUNT')) seq;

-- 6. Генерируем расписание (таблица с индексом)
INSERT INTO vending_point_schedules (vending_point_id, vending_point_schedule_day_week, vending_point_schedule_time_start, vending_point_schedule_time_end)
SELECT
    (random() * (get_config('VENDING_POINTS_COUNT') - 1))::integer + 1,
    (array['monday','tuesday','wednesday','thursday','friday','saturday','sunday'])[floor(random()*7)+1]::day_week_enum,
    '08:00'::time + (random() * 60 * 60 * 4) * interval '1 second',
    '12:00'::time + (random() * 60 * 60 * 8) * interval '1 second'
FROM generate_series(1, get_config('SCHEDULES_COUNT')) seq;

-- 7. Генерируем необычное расписание (таблица с индексом)
INSERT INTO vending_point_unusual_schedules (vending_point_id, vending_point_unusual_schedule_date, vending_point_schedule_time_start, vending_point_schedule_time_end)
SELECT
    (random() * (get_config('VENDING_POINTS_COUNT') - 1))::integer + 1,
    now()::date - (random() * get_config('DAYS_BACK'))::integer,
    '08:00'::time + (random() * 60 * 60 * 4) * interval '1 second',
    '12:00'::time + (random() * 60 * 60 * 8) * interval '1 second'
FROM generate_series(1, get_config('UNUSUAL_SCHEDULES_COUNT')) seq;

-- 8. Генерируем поставки (таблица с индексом по дате)
INSERT INTO machine_supplies (machine_id, machine_supplies_quantity_paper, machine_supplies_ink_level, machine_supplies_datetime)
SELECT
    (random() * (get_config('MACHINES_COUNT') - 1))::integer + 1,
    (random() * 1000)::integer,
    (random() * 100)::integer,
    now() - (random() * get_config('DAYS_BACK') * interval '1 day')
FROM generate_series(1, get_config('MACHINE_SUPPLIES_COUNT')) seq;

-- 9. Генерируем состояния аппаратов (таблица с индексом по дате)
INSERT INTO machine_conditions (machine_id, machine_status, machine_condition_datetime)
SELECT
    (random() * (get_config('MACHINES_COUNT') - 1))::integer + 1,
    (array['work','temporarily_not_work','closed'])[floor(random()*3)+1]::machine_status_enum,
    now() - (random() * get_config('DAYS_BACK') * interval '1 day')
FROM generate_series(1, get_config('MACHINE_CONDITIONS_COUNT')) seq;

-- 10. Генерируем заказы (основная таблица с индексами)
-- ВАЖНО: Используем только существующие account_id из таблицы accounts
INSERT INTO orders (account_id, vending_point_id, order_amount, order_datetime, order_type, order_status, order_num)
SELECT
    a.account_id,
    (random() * (get_config('VENDING_POINTS_COUNT') - 1))::integer + 1,
    (random() * 1000)::numeric,
    now() - (random() * get_config('DAYS_BACK') * interval '1 day'),
    (array['print','scan'])[floor(random()*2)+1]::order_type_enum,
    (array['not_paid','paid','completed'])[floor(random()*3)+1]::order_status_enum,
    seq
FROM generate_series(1, get_config('ORDERS_COUNT')) seq
CROSS JOIN LATERAL (
    SELECT account_id
    FROM accounts
    ORDER BY random()
    LIMIT 1
) a;

-- 11. Генерируем задачи на сканирование
INSERT INTO scan_tasks (order_id, machine_id, scan_task_number_pages)
SELECT
    (random() * (get_config('ORDERS_COUNT') - 1))::integer + 1,
    (random() * (get_config('MACHINES_COUNT') - 1))::integer + 1,
    (random() * 10)::integer + 1
FROM generate_series(1, get_config('SCAN_TASKS_COUNT')) seq;

-- 12. Генерируем задачи на печать
INSERT INTO print_tasks (order_id, machine_id, print_task_color, print_task_number_copies)
SELECT
    (random() * (get_config('ORDERS_COUNT') - 1))::integer + 1,
    (random() * (get_config('MACHINES_COUNT') - 1))::integer + 1,
    (array['black_white','color'])[floor(random()*2)+1]::print_task_color_enum,
    (random() * 10)::integer + 1
FROM generate_series(1, get_config('PRINT_TASKS_COUNT')) seq;

-- 13. Генерируем файлы
INSERT INTO files (user_id, file_name, file_load_datetime, file_oid)
SELECT
    (random() * (get_config('USERS_COUNT') - 1))::integer + 1,
    'file_' || seq || '.txt',
    now() - (random() * get_config('DAYS_BACK') * interval '1 day'),
    seq
FROM generate_series(1, get_config('FILES_COUNT')) seq;

-- 14. Генерируем связи файлов с задачами
INSERT INTO scan_task_files (scan_task_id, file_id)
SELECT
    (random() * (get_config('SCAN_TASKS_COUNT') - 1))::integer + 1,
    (random() * (get_config('FILES_COUNT') - 1))::integer + 1
FROM generate_series(1, get_config('SCAN_TASK_FILES_COUNT')) seq;

INSERT INTO print_task_files (print_task_id, file_id)
SELECT
    (random() * (get_config('PRINT_TASKS_COUNT') - 1))::integer + 1,
    (random() * (get_config('FILES_COUNT') - 1))::integer + 1
FROM generate_series(1, get_config('PRINT_TASK_FILES_COUNT')) seq;

INSERT INTO machine_files (machine_id, file_id)
SELECT
    (random() * (get_config('MACHINES_COUNT') - 1))::integer + 1,
    (random() * (get_config('FILES_COUNT') - 1))::integer + 1
FROM generate_series(1, get_config('MACHINE_FILES_COUNT')) seq;

-- Очищаем временные объекты
DROP FUNCTION get_config(varchar);
DROP TABLE config;
