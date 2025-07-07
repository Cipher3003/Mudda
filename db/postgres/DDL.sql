
CREATE TYPE roles_enum AS ENUM ('MODERATOR', 'ADMIN', 'CITIZEN');

CREATE TABLE roles (
    role_id BIGSERIAL PRIMARY KEY,
    name roles_enum  not null NOT null -- e.g., 'CITIZEN', 'ADMIN'
);

INSERT INTO roles (name) VALUES
('CITIZEN'),
('ADMIN'),
('MODERATOR');


CREATE TABLE categories (
    category_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE -- e.g., Roads, Electricity, Water
);

CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(20) unique not null,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    country_code VARCHAR(10), -- Consider making this NOT NULL if it's part of unique phone identification
    phone_number VARCHAR(10) UNIQUE,
    hashed_password VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) -- Corrected reference to roles(role_id)
);
