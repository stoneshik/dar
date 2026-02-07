-- Авторизация/регистрация
CREATE TABLE IF NOT EXISTS roles (
    role_id serial PRIMARY KEY,
    role_name varchar(100) NOT NULL,
    role_description varchar(400) NOT NULL,
    role_created_datetime timestamp NOT NULL DEFAULT current_timestamp
);
CREATE TYPE user_status_enum AS enum('unverified', 'verified', 'banned');
CREATE TABLE IF NOT EXISTS users (
    user_id serial PRIMARY KEY,
    user_email varchar(200) NOT NULL UNIQUE,
    user_login varchar(100) NOT NULL UNIQUE,
    user_password_hash varchar(200) NOT NULL,
    user_created_datetime timestamp NOT NULL DEFAULT current_timestamp,
    user_status user_status_enum NOT NULL DEFAULT 'unverified'
);
CREATE TABLE IF NOT EXISTS  user_roles (
    user_role_id serial PRIMARY KEY,
    user_id integer NOT NULL REFERENCES users ON DELETE CASCADE,
    role_id integer NOT NULL REFERENCES roles ON DELETE CASCADE
);
-- Счет
CREATE TABLE IF NOT EXISTS accounts (
    account_id serial PRIMARY KEY,
    user_id integer NOT NULL REFERENCES users ON DELETE CASCADE,
    account_balance numeric NOT NULL
);
CREATE TABLE IF NOT EXISTS replenishes (
    replenish_id serial PRIMARY KEY,
    account_id integer NOT NULL REFERENCES accounts ON DELETE CASCADE,
    replenish_amount numeric NOT NULL,
    replenish_datetime timestamp NOT NULL DEFAULT current_timestamp
);
-- Вендинговые точки
CREATE TABLE IF NOT EXISTS vending_points (
    vending_point_id serial PRIMARY KEY,
    vending_point_address text NOT NULL,
    vending_point_description text NOT NULL,
    vending_point_number_machines integer NOT NULL,
    vending_point_cords decimal[] NOT NULL
);
CREATE TYPE day_week_enum AS enum('monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday');
CREATE TABLE IF NOT EXISTS vending_point_schedules (
    vending_point_schedule_id serial PRIMARY KEY,
    vending_point_id integer NOT NULL REFERENCES vending_points ON DELETE CASCADE,
    vending_point_schedule_day_week day_week_enum NOT NULL,
    vending_point_schedule_time_start time NOT NULL,
    vending_point_schedule_time_end time NOT NULL
);
CREATE TABLE IF NOT EXISTS vending_point_unusual_schedules (
    vending_point_unusual_schedule_id serial PRIMARY KEY,
    vending_point_id integer NOT NULL REFERENCES vending_points ON DELETE CASCADE,
    vending_point_unusual_schedule_date date NOT NULL,
    vending_point_schedule_time_start time NOT NULL,
    vending_point_schedule_time_end time NOT NULL
);
-- Вендинговые аппараты (машины)
CREATE TABLE IF NOT EXISTS machines (
    machine_id serial PRIMARY KEY,
    vending_point_id integer NOT NULL REFERENCES vending_points ON DELETE CASCADE
);
CREATE TYPE function_variant_enum AS enum('black_white_print', 'color_print', 'scan');
CREATE TABLE IF NOT EXISTS function_variants (
    function_variant_id serial PRIMARY KEY,
    vending_point_id integer NOT NULL REFERENCES vending_points ON DELETE CASCADE,
    machine_id integer NOT NULL REFERENCES machines ON DELETE CASCADE,
    function_variant function_variant_enum NOT NULL
);
CREATE TABLE IF NOT EXISTS machine_supplies (
    machine_supplies_id serial PRIMARY KEY,
    machine_id integer NOT NULL REFERENCES machines ON DELETE CASCADE,
    machine_supplies_quantity_paper integer NOT NULL,
    machine_supplies_ink_level integer NOT NULL,
    machine_supplies_datetime timestamp NOT NULL DEFAULT current_timestamp
);
CREATE TYPE machine_status_enum AS enum('work', 'temporarily_not_work', 'closed');
CREATE TABLE IF NOT EXISTS machine_conditions (
    machine_condition_id serial PRIMARY KEY,
    machine_id integer NOT NULL REFERENCES machines ON DELETE CASCADE,
    machine_status machine_status_enum NOT NULL,
    machine_condition_datetime timestamp NOT NULL DEFAULT current_timestamp
);
-- Заказы/задания
CREATE TYPE order_type_enum AS enum('print', 'scan');
CREATE TYPE order_status_enum AS enum('not_paid', 'paid', 'completed');
CREATE TABLE IF NOT EXISTS orders (
    order_id serial PRIMARY KEY,
    account_id integer NOT NULL REFERENCES accounts ON DELETE CASCADE,
    vending_point_id integer NOT NULL REFERENCES vending_points ON DELETE CASCADE,
    order_amount numeric NOT NULL,
    order_datetime timestamp NOT NULL DEFAULT current_timestamp,
    order_type order_type_enum NOT NULL,
    order_status order_status_enum NOT NULL,
    order_num integer NOT NULL
);
CREATE TABLE IF NOT EXISTS scan_tasks (
    scan_task_id serial PRIMARY KEY,
    order_id integer NOT NULL REFERENCES orders ON DELETE CASCADE,
    machine_id integer NOT NULL REFERENCES machines ON DELETE CASCADE,
    scan_task_number_pages integer NOT NULL
);
CREATE TYPE print_task_color_enum AS enum('black_white', 'color');
CREATE TABLE IF NOT EXISTS print_tasks (
    print_task_id serial PRIMARY KEY,
    order_id integer NOT NULL REFERENCES orders ON DELETE CASCADE,
    machine_id integer NOT NULL REFERENCES machines ON DELETE CASCADE,
    print_task_color print_task_color_enum NOT NULL,
    print_task_number_copies int NOT NULL
);
-- Файлы
CREATE TABLE IF NOT EXISTS files (
    file_id serial PRIMARY KEY,
    user_id integer NOT NULL REFERENCES users ON DELETE CASCADE,
    file_name varchar(200) NOT NULL,
    file_load_datetime timestamp NOT NULL DEFAULT current_timestamp,
    file_oid oid NOT NULL
);
CREATE TABLE IF NOT EXISTS scan_task_files (
    scan_task_file_id serial PRIMARY KEY,
    scan_task_id integer NOT NULL REFERENCES scan_tasks ON DELETE CASCADE,
    file_id integer NOT NULL REFERENCES files ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS print_task_files (
    print_task_file_id serial PRIMARY KEY,
    print_task_id integer NOT NULL REFERENCES print_tasks ON DELETE CASCADE,
    file_id integer NOT NULL REFERENCES files ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS machine_files (
    machine_file_id serial PRIMARY KEY,
    machine_id integer NOT NULL REFERENCES machines ON DELETE CASCADE,
    file_id integer NOT NULL REFERENCES files ON DELETE CASCADE
);
