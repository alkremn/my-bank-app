CREATE SCHEMA IF NOT EXISTS accounts;
CREATE SCHEMA IF NOT EXISTS notifications;
CREATE SCHEMA IF NOT EXISTS keycloak;

CREATE TABLE IF NOT EXISTS accounts.accounts (
    id          BIGSERIAL PRIMARY KEY,
    login       VARCHAR(255) NOT NULL UNIQUE,
    name        VARCHAR(255),
    birthdate   DATE,
    balance     NUMERIC(19,2) DEFAULT 0
);

INSERT INTO accounts.accounts (login, name, birthdate, balance) VALUES
    ('ivanov', 'Иванов Иван', '2001-01-01', 1000.00),
    ('petrov', 'Петров Петр', '1995-05-15', 500.00);
