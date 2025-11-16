CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE role (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE "user" (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role_id UUID NOT NULL REFERENCES role(id)
);

CREATE TABLE member (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  date_of_birth DATE NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT now() NOT NULL,
  updated_at TIMESTAMP DEFAULT now() NOT NULL
);

INSERT INTO role (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');
INSERT INTO "user" (username, password_hash, role_id)
VALUES ('admin', '$2a$10$Xk32hcGBZpvlgyXa9/Wbo.qkEmDJ/1NSooeSsdDfd' -- bcrypt hash for password,
        (SELECT id FROM role WHERE name = 'ROLE_ADMIN'));
INSERT INTO "user" (username, password_hash, role_id)
VALUES ('user', '$2a$10$Y09Ioc977.hD7gL/1hs1uqI3akdkdowlWPML?[$hfj' -- bcrypt hash for password,
        (SELECT id FROM role WHERE name = 'ROLE_USER'));
