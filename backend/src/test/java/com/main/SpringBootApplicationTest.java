package com.main;

import javax.script.ScriptException;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Testcontainers;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
abstract class SpringBootApplicationTest {
    protected void setupDb(PostgreSQLContainer<?> postgresSqlContainer) throws ScriptException {
        clearDb(postgresSqlContainer);
        createDb(postgresSqlContainer);
        insertDataInDb(postgresSqlContainer);
    }

    protected void setupEmptyDb(PostgreSQLContainer<?> postgresSqlContainer) throws ScriptException {
        clearDb(postgresSqlContainer);
        createDb(postgresSqlContainer);
    }

    protected void clearDb(PostgreSQLContainer<?> postgresSqlContainer) throws ScriptException {
        JdbcDatabaseDelegate containerDelegate = new JdbcDatabaseDelegate(postgresSqlContainer, "");
        ScriptUtils.executeDatabaseScript(containerDelegate, "",
            """
            DROP TRIGGER IF EXISTS create_new_user_trigger ON users;
            DROP TRIGGER IF EXISTS replenish_account_trigger ON replenishes;
            DROP FUNCTION IF EXISTS create_new_user();
            DROP FUNCTION IF EXISTS get_role_id_by_name(varchar);
            DROP FUNCTION IF EXISTS replenish_account();
            DROP TABLE IF EXISTS
                -- Удаление ассоциативных сущностей
                user_roles,
                function_variants,
                scan_task_files,
                print_task_files,
                machine_files,
                -- Остальные сущности
                scan_tasks,
                print_tasks,
                machine_supplies,
                machine_conditions,
                machines,
                vending_point_schedules,
                vending_point_unusual_schedules,
                vending_points,
                orders,
                replenishes,
                accounts,
                files,
                users,
                roles
                ;
            DROP TYPE IF EXISTS
                user_status_enum,
                day_week_enum,
                function_variant_enum,
                machine_status_enum,
                order_type_enum,
                order_status_enum,
                print_task_color_enum
                ;
            """
        );
    }

    protected void createDb(PostgreSQLContainer<?> postgresSqlContainer) throws ScriptException {
        JdbcDatabaseDelegate containerDelegate = new JdbcDatabaseDelegate(postgresSqlContainer, "");
        ScriptUtils.executeDatabaseScript(containerDelegate, "",
            """
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

            -- Создание индексов
            CREATE INDEX user_email_index_hash ON users USING hash(user_email);
            CREATE INDEX user_login_index_hash ON users USING hash(user_login);

            CREATE INDEX order_type_index_btree ON orders USING btree(order_type);
            CREATE INDEX order_status_index_btree ON orders USING btree(order_status);

            CREATE INDEX vending_point_unusual_schedule_date_index_hash ON vending_point_unusual_schedules
                USING hash(vending_point_unusual_schedule_date);

            CREATE INDEX function_variant_index_btree ON function_variants USING btree(function_variant);

            CREATE INDEX machine_supplies_datetime_index_btree ON machine_supplies USING btree(machine_supplies_datetime);
            CREATE INDEX machine_condition_datetime_index_btree ON machine_conditions USING btree(machine_condition_datetime);

            -- триггер для создания нового пользователя
            -- 1) Пользователю назначается роль user
            -- 2) Создается новый счет с нулевым балансом и связывается с пользователем
            CREATE OR REPLACE FUNCTION get_role_id_by_name(varchar(100))
                RETURNS TABLE(role_id integer) AS $$
                SELECT role_id::integer FROM roles WHERE role_name = $1;
            $$ LANGUAGE sql;

            CREATE OR REPLACE FUNCTION create_new_user()
                RETURNS trigger AS $$
            DECLARE
                user_id_var integer;
                role_id_var integer;
            BEGIN
                user_id_var = NEW.user_id;
                role_id_var = get_role_id_by_name('user');
                INSERT INTO user_roles(user_role_id, user_id, role_id) VALUES
                    (default, user_id_var, role_id_var);
                INSERT INTO accounts(account_id, user_id, account_balance) VALUES
                    (default, user_id_var, 0);
                RETURN NEW;
            END
            $$ LANGUAGE plpgsql;


            CREATE OR REPLACE TRIGGER create_new_user_trigger
                AFTER INSERT ON users
                FOR EACH ROW
                EXECUTE FUNCTION create_new_user();

            -- триггер для пополнения счета пользователя
            -- 1) Значение счета обновляется на сумму предыдущего значения со значением суммы пополнения
            CREATE OR REPLACE FUNCTION replenish_account()
                RETURNS trigger AS $$
            DECLARE
                account_id_var integer;
                replenish_amount_var numeric;
            BEGIN
                account_id_var = NEW.account_id;
                replenish_amount_var = NEW.replenish_amount;
                UPDATE accounts set account_balance = (account_balance + replenish_amount_var) WHERE account_id = account_id_var;
                RETURN NEW;
            END
            $$ LANGUAGE plpgsql;


            CREATE OR REPLACE TRIGGER replenish_account_trigger
                AFTER INSERT ON replenishes
                FOR EACH ROW
                EXECUTE FUNCTION replenish_account();
            """
        );
    }

    protected void insertDataInDb(PostgreSQLContainer<?> postgresSqlContainer) throws ScriptException {
        JdbcDatabaseDelegate containerDelegate = new JdbcDatabaseDelegate(postgresSqlContainer, "");
        ScriptUtils.executeDatabaseScript(containerDelegate, "",
            """
            -- Авторизация/регистрация
            INSERT INTO roles(role_id, role_name, role_description, role_created_datetime)
            VALUES
                (default, 'user', 'Базовая роль, для обычного пользователя', default),
                (default, 'moder', 'Роль модератора, работника сервиса, почти не имеет ограничений', default),
                (default, 'admin', 'Роль администратора, владельца сервиса, не имеет ограничений', default)
                ;
            INSERT INTO users(
                user_id,
                user_email,
                user_login,
                user_password_hash,
                user_created_datetime,
                user_status)
            VALUES
                (default,
                'admin@mail.ru',
                'admin',
                '$2a$12$DMCQJpUCLrF.eByReybQXOeYjE1s3WdRWVAbAAe8Vapb1xr0DQ4zq',
                default,
                'verified'),
                (default,
                'aboba@mail.ru',
                'aboba',
                '$2a$12$v2z/WZexAqMuQyUWreqr1u.uy2.CM7I.EdhrMQMSIt5/vqBMdf/Tm',
                default,
                'unverified')
                ;
            INSERT INTO replenishes(replenish_id, account_id, replenish_amount, replenish_datetime)
            VALUES
                (default, 1, 120, default),
                (default, 1, 220, default),
                (default, 2, 750, default),
                (default, 2, 200, default),
                (default, 2, 170, default)
                ;
            -- Вендинговые точки
            INSERT INTO vending_points(
                vending_point_id,
                vending_point_address,
                vending_point_description,
                vending_point_number_machines,
                vending_point_cords)
            VALUES
                (0,
                'Невский проспект, 1/4',
                'Находится рядом с банкоматом',
                2,
                '{59.936846, 30.312185}'),
                (1,
                'Невский пр-кт, 2',
                'Находится рядом с банкоматом',
                2,
                '{59.937594, 30.313631}'),
                (2,
                'Кронверкский проспект, 21/2',
                'Находится во дворе',
                2,
                '{59.956940, 30.319282}'),
                (3,
                'Комендантcкий пр-кт, 34',
                'Находится в подвале рядом с шаурмечной',
                1,
                '{60.021227, 30.243383}')
                ;
            INSERT INTO vending_point_schedules(
                vending_point_schedule_id,
                vending_point_id,
                vending_point_schedule_day_week,
                vending_point_schedule_time_start,
                vending_point_schedule_time_end)
            VALUES
                (default, 0, 'monday', '10:00', '20:00'),
                (default, 0, 'tuesday', '09:00', '21:00'),
                (default, 0, 'wednesday', '11:00', '23:00'),
                (default, 0, 'thursday', '10:00', '20:00'),
                (default, 0, 'friday', '10:00', '20:00'),
                (default, 1, 'saturday', '06:35', '22:20'),
                (default, 1, 'sunday', '09:03', '21:55'),
                (default, 2, 'monday', '10:00', '20:00'),
                (default, 2, 'tuesday', '09:00', '21:00'),
                (default, 2, 'wednesday', '11:00', '23:00'),
                (default, 2, 'thursday', '10:00', '20:00'),
                (default, 2, 'friday', '10:00', '20:00'),
                (default, 3, 'monday', '10:00', '20:00'),
                (default, 3, 'tuesday', '09:00', '21:00'),
                (default, 3, 'wednesday', '11:00', '23:00'),
                (default, 3, 'thursday', '10:00', '20:00'),
                (default, 3, 'friday', '10:00', '20:00')
                ;
            INSERT INTO vending_point_unusual_schedules(
                vending_point_unusual_schedule_id,
                vending_point_id,
                vending_point_unusual_schedule_date,
                vending_point_schedule_time_start,
                vending_point_schedule_time_end)
            VALUES
                (default, 0, '2024-11-15', '02:00', '22:00')
                ;
            -- Вендинговые аппараты (машины)
            INSERT INTO machines(machine_id, vending_point_id)
            VALUES
                (0, 0),
                (1, 0),
                (2, 1),
                (3, 2)
                ;
            INSERT INTO function_variants(function_variant_id, vending_point_id, machine_id, function_variant)
            VALUES
                (default, 0, 0, 'black_white_print'),
                (default, 0, 0, 'color_print'),
                (default, 0, 1, 'black_white_print'),
                (default, 0, 1, 'color_print'),
                (default, 0, 1, 'scan'),
                (default, 1, 2, 'black_white_print'),
                (default, 1, 2, 'scan'),
                (default, 2, 2, 'black_white_print'),
                (default, 3, 3, 'black_white_print'),
                (default, 3, 3, 'scan')
                ;
            INSERT INTO machine_supplies(
                machine_supplies_id,
                machine_id,
                machine_supplies_quantity_paper,
                machine_supplies_ink_level,
                machine_supplies_datetime)
            VALUES
                (default, 0, 100, 900, default),
                (default, 0, 40, 800, default),
                (default, 1, 200, 500, default),
                (default, 2, 800, 1000, default)
                ;
            INSERT INTO machine_conditions(machine_condition_id, machine_id, machine_status, machine_condition_datetime)
            VALUES
                (default, 0, 'work', default),
                (default, 0, 'temporarily_not_work', default),
                (default, 0, 'work', default),
                (default, 1, 'work', default),
                (default, 2, 'temporarily_not_work', default)
                ;
            """
        );
    }
}
