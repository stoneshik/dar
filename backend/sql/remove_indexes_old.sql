-- Удаление индексов таблицы users
DROP INDEX IF EXISTS user_email_index_hash;
DROP INDEX IF EXISTS user_login_index_hash;

-- Удаление индексов таблицы orders
DROP INDEX IF EXISTS order_type_index_hash;
DROP INDEX IF EXISTS order_status_index_hash;

-- Удаление индексов таблицы vending_point_schedules
DROP INDEX IF EXISTS vending_point_schedule_day_week_index_hash;
DROP INDEX IF EXISTS vending_point_unusual_schedule_date_index_hash;

-- Удаление индекса таблицы function_variants
DROP INDEX IF EXISTS function_variant_index_hash;

-- Удаление индексов таблиц machine_supplies и machine_conditions
DROP INDEX IF EXISTS machine_supplies_datetime_index_btree;
DROP INDEX IF EXISTS machine_condition_datetime_index_btree;
