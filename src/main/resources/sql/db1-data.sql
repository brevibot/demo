CREATE TABLE app_users (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255)
);
INSERT INTO app_users (id, name) VALUES (1, 'Alice from DB1');
INSERT INTO app_users (id, name) VALUES (2, 'Bob from DB1');
