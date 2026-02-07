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
