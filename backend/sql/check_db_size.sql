-- Размер базы данных
SELECT pg_size_pretty(pg_database_size(current_database())) as database_size_bytes;
